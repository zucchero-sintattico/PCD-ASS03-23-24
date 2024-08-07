object FileComparator:
  def compareFiles(path1: String, path2: String): Boolean =
    val file1 = scala.io.Source.fromFile(path1)
    val file2 = scala.io.Source.fromFile(path2)
    val lines1 = file1.getLines().toList.map(_.split(":")(1).toDouble)
    val lines2 = file2.getLines().toList.map(_.split(":")(1).toDouble).take(lines1.size)
    file1.close()
    file2.close()
    var eq = true
    for (n1, n2) <- lines1.zip(lines2) do if Math.abs(n1 - n2) > 0.0001 then eq = false
    eq

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





class MassiveTest:

  @Test def massiveTest(): Unit =
    val f = CompletableFuture[Boolean]()
    val system = ActorSystem(TestSystem(f, 100, MASSIVE_SIMULATION), "root")
    f.get()
    val sameResult = FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_massive_improved.txt")
    assertTrue(sameResult)

  @Test def trafficLightTest(): Unit =
    val f = CompletableFuture[Boolean]()
    val system = ActorSystem(TestSystem(f, 500, CROSS_ROADS), "root")
    f.get()
    val sameResult = FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_with_trafficLights_improved.txt")
    assertTrue(sameResult)


object TestSystem:
  def apply(f: CompletableFuture[Boolean], step: Int, st: SimulationType): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val controlView = new SimulationListener():
        override def notifyInit(t: Int, agents: List[Car]): Unit = {}
        override def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit = {}
        override def notifySimulationEnded(simulationDuration: Int): Unit =
          f.complete(true)
          context.system.terminate()
        override def notifyStat(averageSpeed: Double): Unit = {}
        override def simulationStopped(): Unit = {}
      val logView = RoadSimStatistics()
      val viewListenerRelayActor = context.spawn(ViewListenerRelayActor(List(controlView, logView)), "viewListenerRelayActor")
      val simulationHandlerActor = context.spawn(SimulationHandlerActor(viewListenerRelayActor), "simulationHandlerActor")
      simulationHandlerActor ! SimulationHandlerActor.SetupSimulation(st, step, false)
      simulationHandlerActor ! SimulationHandlerActor.StartSimulation
      Behaviors.same
    }