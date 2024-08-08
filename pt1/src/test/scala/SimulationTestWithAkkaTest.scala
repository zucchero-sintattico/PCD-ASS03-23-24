import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import logic.{SimulationHandlerActor, SimulationType}
import logic.SimulationType.{CROSS_ROADS, MASSIVE_SIMULATION}
import utils.RoadSimStatistics
import view.ViewListenerRelayActor
import scala.concurrent.duration.DurationInt

class SimulationTestWithAkkaTest extends AnyFunSuite with Matchers with BeforeAndAfterAll:

  private val testKit = ActorTestKit()
  override def afterAll(): Unit = testKit.shutdownTestKit()

  test("Massive simulation result match expectation"):
    testSimulation(MASSIVE_SIMULATION, 1, 100)
    FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_massive_improved.txt") shouldBe true

  test("TrafficLight simulation result match expectation"):
    testSimulation(CROSS_ROADS, 1, 500)
    FileComparator.compareFiles("log.txt", "src/test/scala/resources/log_with_trafficLights_improved.txt") shouldBe true

  case object CompareResult
  private val compareResultProbe = testKit.createTestProbe[CompareResult.type]()
  private val logger = RoadSimStatistics()
  private val viewListenerRelayActorMockedBehavior = Behaviors.receiveMessage[ViewListenerRelayActor.Command] {
    case ViewListenerRelayActor.SimulationEnded(_) => compareResultProbe.ref ! CompareResult; Behaviors.same
    case ViewListenerRelayActor.Init(t, cars) => logger.notifyInit(t, cars); Behaviors.same
    case ViewListenerRelayActor.Stat(avgSpeed) => logger.notifyStat(avgSpeed); Behaviors.same
    case _ => Behaviors.same
  }
  private val viewListenerRelayActorProbe = testKit.createTestProbe[ViewListenerRelayActor.Command]()
  private val mockedViewListenerRelayActor = testKit.spawn(Behaviors.monitor(viewListenerRelayActorProbe.ref, viewListenerRelayActorMockedBehavior))
  private val simulationHandlerActor = testKit.spawn(SimulationHandlerActor(mockedViewListenerRelayActor))

  private def testSimulation(simulationType: SimulationType, dt: Int, step: Int): Unit =
    simulationHandlerActor ! SimulationHandlerActor.SetupSimulationAndStart(simulationType, dt, step, false)
    compareResultProbe.expectMessage(1.hours, CompareResult)
