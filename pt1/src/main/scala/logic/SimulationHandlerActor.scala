package logic


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Terminated}
import logic.SimulationHandlerActor.{Command, EndSimulation, ResetSimulation, StartSimulation, StopSimulation}
import view.{RoadSimView, ViewListenerRelayActor}


object SimulationHandlerActor:
  sealed trait Command
  case object StartSimulation extends Command
  case object StopSimulation extends Command
  case object ResetSimulation extends Command
  case object EndSimulation extends Command
  final case class SetupSimulation(simulationType: SimulationType, numSteps: Int, showView: Boolean) extends Command
  def apply(viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command]): Behavior[Command] = //todo handle dt better
    Behaviors.receivePartial((context, message) => message match
      case SetupSimulation(simulationType, numSteps, showView) =>
        val view = if showView then Some(RoadSimView()) else None
        for v <- view do viewListenerRelayActor ! ViewListenerRelayActor.Add(v)
        val simulation = context.spawnAnonymous(SimulationActor(1, numSteps, simulationType.simulationSetup, viewListenerRelayActor)) //todo check name conflict
        context.watchWith(simulation, EndSimulation)
        SimulationHandlerActor(simulation, viewListenerRelayActor, view).simulationReady
    )

case class SimulationHandlerActor(simulation: ActorRef[SimulationActor.Command], viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command], viewToDispose: Option[SimulationListener]):
  private def simulationReady: Behavior[Command] =
     Behaviors.receive((context, message) => message match
        case StartSimulation =>
          simulation ! SimulationActor.Start
          simulationRunning
        case ResetSimulation =>
          context.stop(simulation)
          awaitSimulationTermination
//        case EndSimulation =>
//          for view <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(view)
//       //        SimulationHandlerActor(viewListenerRelayActor)

     )
//       case (_, Terminated(_))=>
//         for view <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(view)
//
//     )
  private def awaitSimulationTermination: Behavior[Command] =
    Behaviors.receivePartial { (context, message) => message match
      case EndSimulation => endSimulation
    }

  private def endSimulation: Behavior[Command] =
    for view <- viewToDispose do viewListenerRelayActor ! ViewListenerRelayActor.Remove(view)
    SimulationHandlerActor(viewListenerRelayActor)

  private def simulationRunning: Behavior[Command] =
    Behaviors.receivePartial((context, message) => message match
      case StopSimulation =>
        simulation ! SimulationActor.Stop
        simulationReady
      case EndSimulation => endSimulation
    )
