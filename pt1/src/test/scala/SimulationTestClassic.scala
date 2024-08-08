import akka.NotUsed
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import logic.SimulationType.{MASSIVE_SIMULATION, CROSS_ROADS}
import org.junit.*
import org.junit.Assert.*
import utils.RoadSimStatistics
import logic.{Car, Road, SimulationHandlerActor, SimulationListener, SimulationType, TrafficLight}
import view.ViewListenerRelayActor
import java.util.concurrent.CompletableFuture

class SimulationTestClassic:

  @Test def massiveTest(): Unit =
    val simulationDone = CompletableFuture[Unit]()
    ActorSystem(TestSystem(simulationDone, 1, 100, MASSIVE_SIMULATION), "root")
    simulationDone.get()
    assertTrue(FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_massive_improved.txt"))

  @Test def trafficLightTest(): Unit =
    val simulationDone = CompletableFuture[Unit]()
    ActorSystem(TestSystem(simulationDone, 1, 500, CROSS_ROADS), "root")
    simulationDone.get()
    assertTrue(FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_with_trafficLights_improved.txt"))

  private object TestSystem:
    def apply(simulationDone: CompletableFuture[Unit], dt: Int, step: Int, st: SimulationType): Behavior[NotUsed] =
      Behaviors.setup { context =>
        val controlView = new SimulationListener():
          override def notifyInit(t: Int, agents: List[Car]): Unit = {}
          override def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit = {}
          override def notifySimulationEnded(simulationDuration: Int): Unit =
            simulationDone.complete(())
            context.system.terminate()
          override def notifyStat(averageSpeed: Double): Unit = {}
        val logView = RoadSimStatistics()
        val viewListenerRelayActor = context.spawn(ViewListenerRelayActor(List(controlView, logView)), "viewListenerRelayActor")
        val simulationHandlerActor = context.spawn(SimulationHandlerActor(viewListenerRelayActor), "simulationHandlerActor")
        simulationHandlerActor ! SimulationHandlerActor.SetupSimulationAndStart(st, dt, step, false)
        Behaviors.same
      }