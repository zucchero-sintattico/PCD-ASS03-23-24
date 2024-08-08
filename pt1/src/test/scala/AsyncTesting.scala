import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import logic.SimulationHandlerActor
import logic.SimulationType.{CROSS_ROADS, MASSIVE_SIMULATION}
import view.ViewListenerRelayActor

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case object CompareResult

class AsyncTesting extends AnyWordSpec with Matchers with BeforeAndAfterAll:
  val testKit = ActorTestKit()
  override def afterAll(): Unit = testKit.shutdownTestKit()

  "A Simulation" must {
    "start and stop" in {
      val compareProbe = testKit.createTestProbe[CompareResult.type]()
      val mockedBehavior = Behaviors.receiveMessage[ViewListenerRelayActor.Command]{
        case ViewListenerRelayActor.SimulationEnded(_) =>
          compareProbe.ref ! CompareResult
          Behaviors.same
        case _ => Behaviors.same
      }
      val probe = testKit.createTestProbe[ViewListenerRelayActor.Command]()
      val mockedViewListenerRelayActor = testKit.spawn(Behaviors.monitor(probe.ref, mockedBehavior))
      val simulationHandlerActor = testKit.spawn(SimulationHandlerActor(mockedViewListenerRelayActor))
      simulationHandlerActor ! SimulationHandlerActor.SetupSimulation(MASSIVE_SIMULATION, 1, 100, false)
      simulationHandlerActor ! SimulationHandlerActor.StartSimulation
      compareProbe.expectMessage(1.hours, CompareResult)
      FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_with_trafficLights_improved.txt") shouldBe true
    }
  }
