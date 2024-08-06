package view

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import logic.{Car, Road, SimulationActor, SimulationHandlerActor, SimulationType, TrafficLight, SimulationListener}


import scala.annotation.targetName
import scala.jdk.CollectionConverters.*

object ViewListenerRelayActor:
  sealed trait Command
  final case class Add(simulationListener: SimulationListener) extends Command
  final case class Remove(simulationListener: SimulationListener) extends Command
  final case class Init(t: Int, agents: List[Car]) extends Command
  final case class StepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]) extends Command
  final case class SimulationEnded(simulationDuration: Int) extends Command
  final case class Stat(averageSpeed: Double) extends Command
  def apply(): Behavior[Command] = ViewListenerRelayActor(List())
  //todo handle better add and remove to notify correctly statisticalView
  def apply(views: List[SimulationListener]): Behavior[Command] =
    Behaviors.receiveMessage {
      case Add(sl) =>
        ViewListenerRelayActor(sl :: views)
      case Remove(sl) =>
        sl.simulationStopped()
        ViewListenerRelayActor(views.filter(_ == sl))
      case Init(t, agents) =>
        for view <- views do view.notifyInit(t, agents)
        Behaviors.same
      case StepDone(t, roads, agents, trafficLights) =>
        println("[VIEW] recieve")
        for view <- views do view.notifyStepDone(t, roads, agents, trafficLights)
        Behaviors.same
      case SimulationEnded(simulationDuration) =>
        for view <- views do view.notifySimulationEnded(simulationDuration)
        Behaviors.same
      case Stat(averageSpeed) =>
        for view <- views do view.notifyStat(averageSpeed)
        Behaviors.same
    }


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
          simulationHandlerActor ! SimulationHandlerActor.SetupSimulation(simulationType, numSteps, showView)
          Behaviors.same
      }
    }
