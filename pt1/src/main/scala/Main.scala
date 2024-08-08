import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import logic.SimulationHandlerActor
import utils.RoadSimStatistics
import view.{StatisticalView, ViewClickRelayActor, ViewListenerRelayActor}

object StartSystem:
  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val mainView = StatisticalView()
      val logView = RoadSimStatistics()
      val viewListenerRelayActor = context.spawn(ViewListenerRelayActor(List(mainView, logView)), "viewListenerRelayActor")
      val simulationHandlerActor = context.spawn(SimulationHandlerActor(viewListenerRelayActor), "simulationHandlerActor")
      val viewClickRelayActor = context.spawn(ViewClickRelayActor(mainView, simulationHandlerActor), "viewClickRelayActor")
      Behaviors.same
    }

@main def Main(): Unit =
  val actorSys = ActorSystem(StartSystem(), "root")

