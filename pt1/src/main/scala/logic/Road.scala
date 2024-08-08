package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.RoadActor.{CarRecord, CarStepDone, Command, ProcessStep, Step, TrafficLightRecord, TrafficLightStepDone}
import utils.Point2D

object RoadActor:
  sealed trait Command
  final case class Step(dt: Int, replyTo: ActorRef[SimulationActor.RoadStepDone]) extends Command
  private final case class ProcessStep(dt: Int, replyTo: ActorRef[SimulationActor.RoadStepDone  ], trafficLights: Option[List[TrafficLightRecord]] = Option.empty, cars: Option[List[CarRecord]] = Option.empty) extends Command
  case object TrafficLightStepDone extends Command
  final case class TrafficLightRecord(trafficLightRecord: TrafficLight) extends Command
  final case class CarRecord(carRecord: Car, carRef: ActorRef[CarActor.Command]) extends Command
  final case class CarStepDone(car: Car) extends Command

  def apply(road: Road, trafficLights: List[TrafficLight], cars: List[Car]): Behavior[Command] =
    Behaviors.setup { context =>
      val trafficLightActors = trafficLights.map(tl => context.spawn(TrafficLightActor(tl), tl.agentID))
      val carActors = cars.map(car => context.spawn(CarActor(car), car.agentID))
      RoadActor(road, trafficLightActors, carActors).waitForStepRequest
    }


case class RoadActor(road: Road, trafficLightActors: List[ActorRef[TrafficLightActor.Command]], carActors: List[ActorRef[CarActor.Command]]):
  private def waitForStepRequest: Behavior[Command] =
    Behaviors.setup{ context =>
      Behaviors.receiveMessagePartial {
        case Step(dt, replyTo) =>
          context.spawnAnonymous(
            Aggregator[TrafficLightStepDone.type, ProcessStep](
              sendRequests = replyTo => trafficLightActors.foreach(_ ! TrafficLightActor.Step(dt, replyTo)),
              expectedReplies = trafficLightActors.size,
              replyTo = context.self,
              aggregateReplies = replies => ProcessStep(dt, replyTo)
            )
          )
          askForTrafficLightsRecords
      }
    }
  private def askForTrafficLightsRecords: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case p: ProcessStep =>
          context.spawnAnonymous(
            Aggregator[TrafficLightRecord, ProcessStep](
              sendRequests = replyTo => trafficLightActors.foreach(_ ! TrafficLightActor.RequestTrafficLightRecord(replyTo)),
              expectedReplies = trafficLightActors.size,
              replyTo = context.self,
              aggregateReplies = replies => p.copy(trafficLights = Option(replies))
            )
          )
          askForCarRecords
      }
    }
  private def askForCarRecords: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case p: ProcessStep =>
          context.spawnAnonymous(
            Aggregator[CarRecord, ProcessStep](
              sendRequests = replyTo => carActors.foreach(_ ! CarActor.RequestCarRecord(replyTo)),
              expectedReplies = carActors.size,
              replyTo = context.self,
              aggregateReplies = replies => p.copy(cars = Option(replies))
            )
          )
          evaluatePerceptions
      }
    }

  private def evaluatePerceptions: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case p: ProcessStep =>
          context.spawnAnonymous(
            Aggregator[CarRecord, ProcessStep](
              sendRequests = replyTo =>
                  for
                    cars <- p.cars
                    trafficLights <- p.trafficLights
                    car <- cars
                    carPerception = Road.calculateCarPerception(car, cars, trafficLights)
                  do car.carRef ! CarActor.DecideAction(p.dt, carPerception, replyTo),
              expectedReplies = carActors.size,
              replyTo = context.self,
              aggregateReplies = replies => p.copy(cars = Option(replies.sortBy(_.carRecord.agentID)))
            )
          )
          evaluateActions
      }
    }

  private def evaluateActions: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case p: ProcessStep =>
          context.spawnAnonymous(
            Aggregator[CarStepDone, SimulationActor.RoadStepDone](
              sendRequests =
                {replyTo =>
                  for cars <- p.cars do {
                    var updatedCars = cars
                    cars.foreach(car => {
                      val nc = Road.doAction(car, updatedCars)
                      updatedCars = updatedCars.map(oc => if oc.carRecord.agentID == nc.carRecord.agentID then nc else oc)
                    })
                    updatedCars.foreach(carRecord => carRecord.carRef ! CarActor.UpdateCarRecord(carRecord.carRecord, replyTo))}},
              expectedReplies = carActors.size,
              replyTo = p.replyTo,
              aggregateReplies = replies => SimulationActor.RoadStepDone(road, replies.map(_.car).sortBy(_.agentID), p.trafficLights.get.map(_.trafficLightRecord))
            )
          )
          waitForStepRequest
      }
    }
case class RoadBuildData(road: Road, trafficLights: List[TrafficLight], cars: List[Car])

object Road:

  val minDistAllowed = 5
  val carDetectionRange = 30
  val trafficLightDetectionRange = 30

  def calculateCarPerception(car: CarRecord, cars: List[CarRecord], trafficLights: List[TrafficLightRecord]): CarPerception =
    CarPerception(car.carRecord.position, findNearestCarInFront(car, cars), findNearestTrafficLightInFront(car, trafficLights))

  private def findNearestCarInFront(car: CarRecord, cars: List[CarRecord]): Option[Car] =
    val crs = cars.map(_.carRecord)
      .filter(otherCar => otherCar.position > car.carRecord.position && otherCar.position - car.carRecord.position <= carDetectionRange)
      .sortBy(_.position).headOption
    crs

  private def findNearestTrafficLightInFront(car: CarRecord, trafficLights: List[TrafficLightRecord]): Option[TrafficLight] =
    val tls = trafficLights.map(_.trafficLightRecord)
      .filter(trafficLight => trafficLight.trafficLightPositionInfo.roadPosition > car.carRecord.position)
    if tls.isEmpty then Option.empty
    else Option(tls.min((c1,c2) => Math.round(c1.trafficLightPositionInfo.roadPosition - c2.trafficLightPositionInfo.roadPosition).toInt))

  def doAction(car: CarRecord, cars: List[CarRecord]): CarRecord =
    car.carRecord.selectedAction match
      case Some(action: MoveForward) =>
          val nearestCarInFront = findNearestCarInFront(car, cars)
          var newPosition = car.carRecord.position
          nearestCarInFront match
            case Some(nearestCarInFront) =>
              val distanceToNearestCar = nearestCarInFront.position - car.carRecord.position
              if distanceToNearestCar > action.distance + minDistAllowed then
                newPosition = car.carRecord.position + action.distance
            case None =>
              newPosition = car.carRecord.position + action.distance
          if newPosition > car.carRecord.road.length then
            newPosition = 0
          car.copy(carRecord = car.carRecord.updatePositionAndRemoveAction(newPosition))
      case _ => car

case class Road(agentID: String, startPoint: Point2D, endPoint: Point2D):
  def length: Double = Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2))