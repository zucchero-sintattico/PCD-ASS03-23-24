package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object CarActor:

  sealed trait Command
  final case class GetPosition(replyTo: ActorRef[RoadActor.CarPosition]) extends Command
  final case class DecideAction(dt: Int, carPerception: CarPerception, replyTo: ActorRef[RoadActor.CarAction]) extends Command
  final case class UpdatePosition(position: Double) extends Command
  
  def apply(car: Car): Behavior[Command] =
    Behaviors.receive { (context, message) => message match
      case GetPosition(replyTo) =>
        replyTo ! RoadActor.CarPosition(car.agentID, car.configuration.position, context.self)
        Behaviors.same

    }

sealed trait Action
case class MoveForward(distance: Double) extends Action

case class CarAgentConfiguration(position: Double, acceleration: Double, deceleration: Double, maxSpeed: Double, road: Road)

case class CarPerception(roadPosition: Double)

enum BaseCarAgentState:
  case STOPPED, ACCELERATING, DECELERATING_BECAUSE_OF_A_CAR, WAIT_A_BIT, MOVING_CONSTANT_SPEED

trait Car:
  val agentID: String
  val configuration: CarAgentConfiguration
  val selectedAction: Option[Action]
  def decide(dt: Int, carPerception: CarPerception): Car

case class BaseCarAgent(agentID: String, configuration: CarAgentConfiguration, selectedAction: Option[Action] = None) extends Car:
  override def decide(dt: Int, carPerception: CarPerception): Car = this.copy(selectedAction = Some(MoveForward(1.0))) //TODO improve