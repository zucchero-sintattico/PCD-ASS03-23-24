package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.RoadActor.{CarRecord, Command, ProcessStep, Step, TrafficLightRecord, TrafficLightStepDone}
import utils.Point2D

import scala.concurrent.duration.DurationInt

object RoadActor:

  sealed trait Command
  final case class Step(dt: Int, replyTo: ActorRef[SimulationActor.RoadStepDone.type]) extends Command
  private final case class ProcessStep(dt: Int, replyTo: ActorRef[SimulationActor.RoadStepDone.type], trafficLights: Option[List[TrafficLightRecord]] = Option.empty, cars: Option[List[CarRecord]] = Option.empty) extends Command

  case object TrafficLightStepDone extends Command
  final case class TrafficLightRecord(trafficLightRecord: TrafficLight) extends Command
  final case class CarRecord(carRecord: Car, carRef: ActorRef[CarActor.Command]) extends Command
  case object CarStepDone extends Command

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
              aggregateReplies = replies => ProcessStep(dt, replyTo),
              timeout = 5.seconds
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
              expectedReplies = carActors.size,
              replyTo = context.self,
              aggregateReplies = replies => p.copy(trafficLights = Option(replies.toList)),
              timeout = 5.seconds
            )
          )
          evaluatePerceptions
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
              aggregateReplies = replies => p.copy(cars = Option(replies.toList)),
              timeout = 5.seconds
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
              aggregateReplies = replies => p.copy(cars = Option(replies.sortBy(_.carRecord.agentID).toList)),
              timeout = 5.seconds
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
            Aggregator[CarRecord, SimulationActor.RoadStepDone.type](
              sendRequests = replyTo =>
                for
                  cars <- p.cars
                  trafficLights <- p.trafficLights
                  car <- cars
                  carPerception = Road.calculateCarPerception(car, cars, trafficLights)
                do car.carRef ! CarActor.DecideAction(p.dt, carPerception, replyTo),
              expectedReplies = carActors.size,
              replyTo = p.replyTo,
              aggregateReplies = _ => SimulationActor.RoadStepDone,
              timeout = 5.seconds
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
    cars.map(_.carRecord)
      .filterNot(_.agentID == car.carRecord.agentID)
      .filter(otherCar => otherCar.position > car.carRecord.position && otherCar.position - car.carRecord.position < carDetectionRange)
      .sortBy(_.position).headOption

  private def findNearestTrafficLightInFront(car: CarRecord, trafficLights: List[TrafficLightRecord]): Option[TrafficLight] =
    trafficLights.map(_.trafficLightRecord)
      .filter(trafficLight => trafficLight.trafficLightPositionInfo.roadPosition > car.carRecord.position && trafficLight.trafficLightPositionInfo.roadPosition - car.carRecord.position < trafficLightDetectionRange)
      .sortBy(_.trafficLightPositionInfo.roadPosition).headOption

  private def doAction(car: CarRecord, cars: List[CarRecord]): CarRecord =
    car.carRecord.selectedAction match
      case Some(action: MoveForward) =>
          val nearestCarInFront = findNearestCarInFront(car, cars)
          var newPosition = 0.0
          nearestCarInFront match
            case Some(nearestCarInFront) =>
              val distanceToNearestCar = nearestCarInFront.position - car.carRecord.position
              if distanceToNearestCar > action.distance + minDistAllowed then
                newPosition = car.carRecord.position + action.distance
            case None => newPosition = car.carRecord.position + action.distance
          if newPosition > car.carRecord.road.length then newPosition = 0
          car.copy(carRecord = car.carRecord.updatePosition(newPosition))
      case _ => car

case class Road(agentID: String, startPoint: Point2D, endPoint: Point2D):
  def length: Double = Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2))