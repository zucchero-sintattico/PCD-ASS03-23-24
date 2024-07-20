package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.RoadActor.CarRecord

object CarActor:

  sealed trait Command
  final case class RequestCarRecord(replyTo: ActorRef[RoadActor.CarRecord]) extends Command
  final case class DecideAction(dt: Int, carPerception: CarPerception, replyTo: ActorRef[RoadActor.CarAction]) extends Command
  final case class UpdatePosition(position: Double) extends Command
  
  def apply(car: Car): Behavior[Command] =
    Behaviors.receive { (context, message) => message match
      case RequestCarRecord(replyTo) =>
        replyTo ! RoadActor.CarRecord(car, context.self)
        Behaviors.same

    }

sealed trait Action
case class MoveForward(distance: Double) extends Action

case class CarAgentConfiguration(acceleration: Double, deceleration: Double, maxSpeed: Double)

case class CarPerception(roadPosition: Double, nearestCarInFront: Option[Car], nearestTrafficLight: Option[TrafficLight])

enum BaseCarAgentState:
  case STOPPED, ACCELERATING, DECELERATING_BECAUSE_OF_A_CAR, WAIT_A_BIT, MOVING_CONSTANT_SPEED

trait Car:
  val agentID: String
  val position: Double
  val road: Road
  val configuration: CarAgentConfiguration
  val selectedAction: Option[Action]
  def decide(dt: Int, carPerception: CarPerception): Car

case class BaseCarAgent(agentID: String, position: Double, road: Road, configuration: CarAgentConfiguration, selectedAction: Option[Action] = None) extends Car:
  override def decide(dt: Int, carPerception: CarPerception): Car = this.copy(selectedAction = Some(MoveForward(1.0))) //TODO improve