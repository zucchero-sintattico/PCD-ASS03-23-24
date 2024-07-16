package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.RoadActor.{CarAction, CarPosition, Command, DoCarStep, EvaluateAction, EvaluatePerception, Step, TrafficLightStepDone}
import utils.Point2D

import scala.concurrent.duration.DurationInt

object RoadActor:
  
  sealed trait Command
  final case class Step(dt: Int) extends Command
  case object TrafficLightStepDone extends Command
  private final case class DoCarStep(dt: Int) extends Command
  final case class CarPosition(id: String, carPosition: Double, car: ActorRef[CarActor.Command]) extends Command
  private final case class EvaluatePerception(positions: List[CarPosition], dt: Int) extends Command
  final case class CarAction(id: String, action: Action, car: ActorRef[CarActor.Command]) extends Command
  private final case class EvaluateAction(positions: List[CarPosition], actions: List[CarAction], dt: Int) extends Command
  
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
        case Step(dt) =>
          context.spawnAnonymous(
            Aggregator[TrafficLightStepDone.type, DoCarStep](
              sendRequests = replyTo => trafficLightActors.foreach(_ ! TrafficLightActor.Step(dt, replyTo)),
              expectedReplies = trafficLightActors.size,
              replyTo = context.self,
              aggregateReplies = replies => DoCarStep(dt),
              timeout = 5.seconds
            )
          )
          askForCarPosition
      }
    }
  
  private def askForCarPosition: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case DoCarStep(dt) =>
          context.spawnAnonymous(
            Aggregator[CarPosition, EvaluatePerception](
              sendRequests = replyTo => carActors.foreach(_ ! CarActor.GetPosition(replyTo)),
              expectedReplies = carActors.size,
              replyTo = context.self,
              aggregateReplies = replies => EvaluatePerception(replies.toList, dt),
              timeout = 5.seconds
            )
          )
          evaluatePerceptions
      }
    }
  
  private def evaluatePerceptions: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case EvaluatePerception(positions: List[CarPosition], dt: Int) =>
          context.spawnAnonymous(
            Aggregator[CarAction, EvaluateAction](
              sendRequests = replyTo => positions.foreach(p => p.car ! CarActor.DecideAction(dt, CarPerception(0), replyTo)),
              expectedReplies = carActors.size,
              replyTo = context.self,
              aggregateReplies = replies => EvaluateAction(positions, replies.sortBy(_.id).toList, dt),
              timeout = 5.seconds
            )
          )
          evaluateActions
      }
    }
    
  private def evaluateActions: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case EvaluateAction(positions: List[CarPosition], actions: List[CarAction], dt: Int) =>
          val updatedCars = positions.zip(actions).map { case (p, a) => a.car ! CarActor.UpdatePosition(p.carPosition + 1) }
          Behaviors.same
      }
    }

case class Road(startPoint: Point2D, endPoint: Point2D):
  def length: Double = Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2))