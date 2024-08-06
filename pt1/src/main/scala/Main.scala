import akka.NotUsed
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import logic.{BaseCarAgent, CarAgentConfiguration, Road, RoadBuildData, SimulationActor, SimulationExample, SimulationHandlerActor, TrafficLight, TrafficLightActor, TrafficLightPositionInfo, TrafficLightState, TrafficLightTimingSetup}
import utils.{Point2D, RoadSimStatistics}
import view.{RoadSimView, StatisticalView, ViewClickRelayActor, ViewListenerRelayActor}

object StartSystem:
  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val mainView = StatisticalView()
      val logView = RoadSimStatistics()
      val viewListenerRelayActor = context.spawn(ViewListenerRelayActor(List(mainView, logView)), "viewListenerRelayActor")
      val simulationHandlerActor = context.spawn(SimulationHandlerActor(viewListenerRelayActor), "simulationHandlerActor")
      val viewClickRelayActor = context.spawn(ViewClickRelayActor(mainView, simulationHandlerActor), "viewClickRelayActor")
//      val sim = context.spawn(SimulationActor(1,100,SimulationExample.trafficSimulationSingleRoadTwoCars), "simulationActor")
//      sim ! SimulationActor.Start
      Behaviors.same
    }

@main def Main(): Unit =
  val sys = ActorSystem(StartSystem(), "root")
  println("ASS03")

