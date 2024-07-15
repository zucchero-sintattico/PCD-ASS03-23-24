package logic


import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.concurrent.duration.FiniteDuration
import logic.TrafficLight.TrafficLight

import scala.util.{Failure, Success}

object Car:
  sealed trait Command
  final case class step(dt: Int) extends Command
  final case class getCurrentPerception(carPerception: CarPerception) extends Command
  case class CarAgentConfiguration(initialPosition: Double, acceleration: Double, deceleration: Double, maxSpeed: Double)
  case class CarPerception(roadPosition: Double)
  object CarActor:
    def apply(car: Car): Behavior[Command] =
      Behaviors.receive { (context, message) => message match
        case step(dt) =>
          implicit val timeout: Timeout = FiniteDuration(3, "second")
          context.ask(car.parentRoad, Road.getCurrentPerception.apply) {
            case Success(message) => message
            case _ => ???
          }
          Behaviors.same
        case getCurrentPerception(carPerception) => ???
      }
  case class Car(agentID: String, parentRoad: ActorRef[Road.Command], configuration: CarAgentConfiguration):
    private var selectedAction = None
