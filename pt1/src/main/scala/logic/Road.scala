package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.RoadActor.{CarAction, CarRecord, Command, ProcessStep, Step, TrafficLightRecord, TrafficLightStepDone}
import utils.Point2D

import scala.concurrent.duration.DurationInt

object RoadActor:

  sealed trait Command
  final case class Step(dt: Int, replyTo: ActorRef[SimulationActor.RoadStepDone.type]) extends Command
  private final case class ProcessStep(dt: Int, replyTo: ActorRef[SimulationActor.RoadStepDone.type], trafficLights: Option[List[TrafficLightRecord]] = Option.empty, cars: Option[List[CarRecord]] = Option.empty, carActions: Option[List[CarAction]] = Option.empty) extends Command

  case object TrafficLightStepDone extends Command
  final case class TrafficLightRecord(trafficLightRecord: TrafficLight) extends Command
  final case class CarRecord(carRecord: Car, carRef: ActorRef[CarActor.Command]) extends Command
  final case class CarAction(id: String, action: Action, car: ActorRef[CarActor.Command]) extends Command

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
            Aggregator[CarAction, ProcessStep](
              sendRequests = replyTo =>
                  for
                    cars <- p.cars
                    trafficLights <- p.trafficLights
                    car <- cars
                    carPerception = Road.calculateCarPerception(car, cars, trafficLights)
                  do car.carRef ! CarActor.DecideAction(p.dt, carPerception, replyTo),
              expectedReplies = carActors.size,
              replyTo = context.self,
              aggregateReplies = replies => p.copy(carActions = Option(replies.sortBy(_.id).toList)),
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
          //todo updatecarspositions
          p.replyTo ! SimulationActor.RoadStepDone
          Behaviors.same
      }
    }
case class RoadBuildData(road: Road, trafficLights: List[TrafficLight], cars: List[Car])

object Road:

  def calculateCarPerception(car: CarRecord, cars: List[CarRecord], trafficLights: List[TrafficLightRecord]): CarPerception =
    CarPerception(car.carRecord.position, findNearestCarInFront(car, cars), findNearestTrafficLightInFront(car, trafficLights))

  private def findNearestCarInFront(car: CarRecord, cars: List[CarRecord]): Option[Car] =
    cars.map(_.carRecord)
      .filterNot(_.agentID == car.carRecord.agentID)
      .filter(_.position > car.carRecord.position)
      .sortBy(_.position).headOption

  private def findNearestTrafficLightInFront(car: CarRecord, trafficLights: List[TrafficLightRecord]): Option[TrafficLight] =
    trafficLights.map(_.trafficLightRecord)
      .filter(_.trafficLightPositionInfo.roadPosition > car.carRecord.position)
      .sortBy(_.trafficLightPositionInfo.roadPosition).headOption


case class Road(agentID: String, startPoint: Point2D, endPoint: Point2D):
  def length: Double = Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2))