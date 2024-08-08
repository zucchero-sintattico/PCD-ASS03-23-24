package logic


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import logic.SimulationHandlerActor.{Command, EndSimulation, ResetSimulation, SetupSimulationAndStart, RestartSimulation, StopSimulation}
import view.{RoadSimView, ViewListenerRelayActor}

import scala.concurrent.duration.FiniteDuration


object SimulationHandlerActor:
  sealed trait Command
  case object RestartSimulation extends Command
  case object StopSimulation extends Command
  case object ResetSimulation extends Command
  case object EndSimulation extends Command
  final case class SetupSimulationAndStart(simulationType: SimulationType, dt: Int, numSteps: Int, showView: Boolean, delay: Option[FiniteDuration] = Option.empty) extends Command
  def apply(viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command], viewToDispose: Option[SimulationListener] = Option.empty): Behavior[Command] =
    Behaviors.receivePartial((context, message) => message match
      case SetupSimulationAndStart(simulationType, dt, numSteps, showView, delayOpt) =>
        for v <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(v)
        val view = if showView then Some(RoadSimView()) else None
        for v <- view do viewListenerRelayActor ! ViewListenerRelayActor.Add(v)
        val roadsBuildData = simulationType.simulationSetup
        val simulation = delayOpt match
          case Some(delay) => context.spawnAnonymous(SimulationActor(dt, numSteps, delay, roadsBuildData, viewListenerRelayActor)) //todo check name conflict
          case _ =>context.spawnAnonymous(SimulationActor(dt, numSteps, simulationType.simulationSetup, viewListenerRelayActor))
        context.watchWith(simulation, EndSimulation)
        viewListenerRelayActor ! ViewListenerRelayActor.Init(0, roadsBuildData.flatMap(_.cars))
        simulation ! SimulationActor.Start
        SimulationHandlerActor(simulation, viewListenerRelayActor, viewToDispose).simulationRunning
    )

case class SimulationHandlerActor(simulation: ActorRef[SimulationActor.Command], viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command], viewToDispose: Option[SimulationListener]):

  private val startTime = System.currentTimeMillis()

  private def simulationReady: Behavior[Command] =
     Behaviors.receive((context, message) => message match
        case RestartSimulation =>
          simulation ! SimulationActor.Start
          simulationRunning
        case ResetSimulation =>
          context.stop(simulation)
          for v <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(v)
          awaitSimulationTermination
     )

  private def awaitSimulationTermination: Behavior[Command] =
    Behaviors.receivePartial { (context, message) => message match
      case EndSimulation =>
        viewListenerRelayActor ! ViewListenerRelayActor.SimulationEnded((System.currentTimeMillis() - startTime).toInt) //todo is needed here?
        SimulationHandlerActor(viewListenerRelayActor, viewToDispose)
    }

  private def simulationRunning: Behavior[Command] =
    Behaviors.receivePartial((context, message) => message match
      case StopSimulation =>
        simulation ! SimulationActor.Stop
        simulationReady
      case EndSimulation =>
        viewListenerRelayActor ! ViewListenerRelayActor.SimulationEnded((System.currentTimeMillis() - startTime).toInt)
        SimulationHandlerActor(viewListenerRelayActor, viewToDispose)
    )
