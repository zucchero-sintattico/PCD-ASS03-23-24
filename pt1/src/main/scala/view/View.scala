package view

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import logic.{Car, Road, SimulationHandlerActor, SimulationListener, SimulationType, TrafficLight}
import scala.concurrent.duration.FiniteDuration

trait Disposable:
  def dispose(): Unit

trait DisposableSimulationListener extends SimulationListener with Disposable

object ViewListenerRelayActor:
  sealed trait Command
  final case class Add(simulationListener: SimulationListener) extends Command
  final case class Remove(simulationListener: DisposableSimulationListener) extends Command
  case object Init extends Command
  final case class StepDone(step: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]) extends Command
  final case class SimulationEnded(simulationDuration: Int) extends Command
  final case class Stat(averageSpeed: Double) extends Command

  def apply(): Behavior[Command] = ViewListenerRelayActor(List())

  def apply(views: List[SimulationListener]): Behavior[Command] =
    Behaviors.receiveMessage {
      case Add(sl) =>
        ViewListenerRelayActor(sl :: views)
      case Remove(sl) =>
        sl.dispose()
        ViewListenerRelayActor(views.filter(_ != sl))
      case Init =>
        for view <- views do view.notifyInit()
        Behaviors.same
      case StepDone(step, roads, agents, trafficLights) =>
        for view <- views do view.notifyStepDone(step, roads, agents, trafficLights)
        Behaviors.same
      case SimulationEnded(simulationDuration) =>
        for view <- views do view.notifySimulationEnded(simulationDuration)
        Behaviors.same
      case Stat(averageSpeed) =>
        for view <- views do view.notifyStat(averageSpeed)
        Behaviors.same
    }

trait Clickable:
  def whenClicked(onClick: ViewClickRelayActor.Command => Unit): Unit

object ViewClickRelayActor:
  sealed trait Command
  case object RestartSimulation extends Command
  case object StopSimulation extends Command
  case object ResetSimulation extends Command
  final case class SetupSimulationAndStart(simulationType: SimulationType, dt: Int, numSteps: Int, showView: Boolean, delay: FiniteDuration) extends Command

  def apply(view: Clickable, simulationHandlerActor: ActorRef[SimulationHandlerActor.Command]): Behavior[Command] =
    Behaviors.setup { context =>
      view.whenClicked(context.self ! _)
      Behaviors.receiveMessage {
        case RestartSimulation =>
          simulationHandlerActor ! SimulationHandlerActor.RestartSimulation
          Behaviors.same
        case StopSimulation =>
          simulationHandlerActor ! SimulationHandlerActor.StopSimulation
          Behaviors.same
        case ResetSimulation =>
          simulationHandlerActor ! SimulationHandlerActor.ResetSimulation
          Behaviors.same
        case SetupSimulationAndStart(simulationType, dt, numSteps, showView, delay) =>
          simulationHandlerActor ! SimulationHandlerActor.SetupSimulationAndStart(simulationType, dt, numSteps, showView, Option(delay))
          Behaviors.same
      }
    }