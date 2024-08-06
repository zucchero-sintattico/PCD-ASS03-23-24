package logic

import akka.actor.typed.Behavior

object SimulationHandlerActor {
  sealed trait Command
  case object StartSimulation extends Command
  case object StopSimulation extends Command
  final case class SetupSimulation(simulationType: SimulationType, numSteps: Int) extends Command
  def apply(): Behavior[Command] = ???
}
