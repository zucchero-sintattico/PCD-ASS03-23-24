package logic


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import logic.SimulationHandlerActor.{Command, EndSimulation, ResetSimulation, SetupSimulation, StartSimulation, StopSimulation}
import view.{RoadSimView, ViewListenerRelayActor}

import scala.concurrent.duration.FiniteDuration


object SimulationHandlerActor:
  sealed trait Command
  case object StartSimulation extends Command
  case object StopSimulation extends Command
  case object ResetSimulation extends Command
  case object EndSimulation extends Command
  final case class SetupSimulation(simulationType: SimulationType, dt: Int, numSteps: Int, showView: Boolean, delay: Option[FiniteDuration] = Option.empty) extends Command
  def apply(viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command]): Behavior[Command] = SimulationHandlerActor(Option.empty, viewListenerRelayActor, Option.empty).awaitSimulationSetup


case class SimulationHandlerActor(simulation: Option[ActorRef[SimulationActor.Command]], viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command], viewToDispose: Option[SimulationListener]):

  private def awaitSimulationSetup: Behavior[Command] =
    Behaviors.receivePartial((context, message) => message match
      case SetupSimulation(simulationType, dt, numSteps, showView, delayOpt) =>
        for v <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(v)
        val view = if showView then Some(RoadSimView()) else None
        for v <- view do viewListenerRelayActor ! ViewListenerRelayActor.Add(v)
        val simulation = delayOpt match
          case Some(delay) => context.spawnAnonymous(SimulationActor(dt, numSteps, delay, simulationType.simulationSetup, viewListenerRelayActor)) //todo check name conflict
          case _ =>context.spawnAnonymous(SimulationActor(dt, numSteps, simulationType.simulationSetup, viewListenerRelayActor))
        context.watchWith(simulation, EndSimulation)
        copy(simulation = Option(simulation), viewToDispose = view).simulationReady
    )


  private def simulationReady: Behavior[Command] =
     Behaviors.receive((context, message) => message match
        case StartSimulation =>
          simulation.get ! SimulationActor.Start
          simulationRunning
        case ResetSimulation =>
          context.stop(simulation.get)
          for v <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(v)
          awaitSimulationTermination
     )

  private def awaitSimulationTermination: Behavior[Command] =
    Behaviors.receivePartial { (context, message) => message match
      case EndSimulation => awaitSimulationSetup
    }

  private def simulationRunning: Behavior[Command] =
    Behaviors.receivePartial((context, message) => message match
      case StopSimulation =>
        simulation.get ! SimulationActor.Stop
        simulationReady
      case EndSimulation => awaitSimulationSetup
    )
