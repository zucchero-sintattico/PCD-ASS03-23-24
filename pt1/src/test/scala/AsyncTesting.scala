import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import logic.SimulationHandlerActor
import logic.SimulationType.CROSS_ROADS
import view.ViewListenerRelayActor

class AsyncTesting extends AnyWordSpec with Matchers with BeforeAndAfterAll:
  val testKit = ActorTestKit()
  override def afterAll(): Unit = testKit.shutdownTestKit()

  "A Simulation" must {
    "start and stop" in {
      val probe = testKit.createTestProbe[ViewListenerRelayActor.Command]()
      val simulationHandlerActor = testKit.spawn(SimulationHandlerActor(probe.ref))
      simulationHandlerActor ! SimulationHandlerActor.SetupSimulation(CROSS_ROADS, 1, 500, false)
      simulationHandlerActor ! SimulationHandlerActor.StartSimulation
      probe.expectMessageType[ViewListenerRelayActor.Init]
      for _ <- 1 to 1000 do probe.expectMessageType[ViewListenerRelayActor.Command]
      probe.expectMessageType[ViewListenerRelayActor.SimulationEnded]
      FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_with_trafficLights_improved.txt") shouldBe true
    }
  }
