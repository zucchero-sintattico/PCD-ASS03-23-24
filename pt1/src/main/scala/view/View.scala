package view

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import logic.{Car, Road, SimulationActor, SimulationHandlerActor, SimulationType, TrafficLight, SimulationListener}


import scala.annotation.targetName
import scala.jdk.CollectionConverters.*

object ViewListenerRelayActor:

  val viewServiceKey: ServiceKey[Command] = ServiceKey[Command]("View")

  sealed trait Command
  case class Init(t: Int, agents: List[Car]) extends Command
  case class StepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]) extends Command
  case class SimulationEnded(simulationDuration: Int) extends Command
  case class Stat(averageSpeed: Double) extends Command
  def apply(): Behavior[Command] = ViewListenerRelayActor(View(List()))
  def apply(view: View): Behavior[Command] =
    Behaviors.setup { context =>
      context.system.receptionist ! Receptionist.Register(viewServiceKey, context.self)
      Behaviors.receiveMessage {
        case Init(t, agents) =>
          for view <- view.views do view.notifyInit(t, agents)
          Behaviors.same
        case StepDone(t, roads, agents, trafficLights) =>
          println("[VIEW] recieve")
          for view <- view.views do view.notifyStepDone(t, roads, agents, trafficLights)
          Behaviors.same
        case SimulationEnded(simulationDuration) =>
          for view <- view.views do view.notifySimulationEnded(simulationDuration)
          Behaviors.same
        case Stat(averageSpeed) =>
          for view <- view.views do view.notifyStat(averageSpeed)
          Behaviors.same
      }
    }



object View:
  def apply(viewConstructors: List[() => SimulationListener]): View =
    new View(viewConstructors.map(_()))

private class View (val views: List[SimulationListener])


// Define the behavior of the ViewActor here
trait Clickable:
  def whenClicked(onClick: ViewClickRelayActor.Command => Unit): Unit


object ViewClickRelayActor:
  trait Command
  case object StartSimulation extends Command
  case object StopSimulation extends Command
  final case class SetupSimulation(simulationType: SimulationType, numSteps: Int, showView: Boolean) extends Command
  def apply(view: Clickable, simulationHandlerActor: ActorRef[SimulationHandlerActor.Command]): Behavior[Command] =
    Behaviors.setup { context =>
      view.whenClicked(context.self ! _)
      Behaviors.receiveMessage {
        case StartSimulation =>
          simulationHandlerActor ! SimulationHandlerActor.StartSimulation
          Behaviors.same
        case StopSimulation =>
          simulationHandlerActor ! SimulationHandlerActor.StopSimulation
          Behaviors.same
        case SetupSimulation(simulationType, numSteps, showView) =>
          //todo handle show view
          simulationHandlerActor ! SimulationHandlerActor.SetupSimulation(simulationType, numSteps)
          Behaviors.same
      }
    }
