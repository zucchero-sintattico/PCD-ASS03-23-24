package view

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import logic.{Car, Road, TrafficLight}
import view.ViewActor.Command

import scala.jdk.CollectionConverters.*

object ViewActor:

  val viewServiceKey = ServiceKey[Command]("View")

  sealed trait Command
  case class Init(t: Int, agents: List[Car]) extends Command
  case class StepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]) extends Command
  case class SimulationEnded() extends Command
  case class Stat(averageSpeed: Double) extends Command

  def apply(view: RoadSimView): Behavior[Command] =
    Behaviors.setup { context =>
      context.system.receptionist ! Receptionist.Register(viewServiceKey, context.self)
      Behaviors.receiveMessage {
        case Init(t, agents) =>
          view.notifyInit(t, agents.asJava)
          Behaviors.same
        case StepDone(t, roads, agents, trafficLights) =>
          view.notifyStepDone(t, roads.asJava, agents.asJava, trafficLights.asJava)
          Behaviors.same
        case SimulationEnded() =>
          view.notifySimulationEnded()
          Behaviors.same
        case Stat(averageSpeed) =>
          view.notifyStat(averageSpeed)
          Behaviors.same
      }
    }


// Define the behavior of the ViewActor here