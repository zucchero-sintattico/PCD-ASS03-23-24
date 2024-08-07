package logic

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.SimulationActor.{Command, RoadStepDone, Step}
import utils.Point2D
import view.ViewListenerRelayActor

import scala.concurrent.duration.DurationInt

object SimulationActor:
  sealed trait Command
  case object Start extends Command
  case object Stop extends Command
  private case class Step(dt: Int, viewMsg: List[ViewListenerRelayActor.Command] = List()) extends Command
  final case class RoadStepDone(road: Road, cars: List[Car], trafficLights: List[TrafficLight]) extends Command

  def apply(dt: Int, numStep: Int, roadsBuildData: List[RoadBuildData], viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command]): Behavior[Command] =
    Behaviors.setup { context =>
      val roadActors = roadsBuildData.map(rbd => context.spawn(RoadActor(rbd.road, rbd.trafficLights, rbd.cars), rbd.road.agentID))
      Behaviors.receiveMessagePartial{
        case Start =>
          context.self ! Step(dt)
          viewListenerRelayActor ! ViewListenerRelayActor.Init(0, roadsBuildData.flatMap(_.cars))
          SimulationActor(roadActors, viewListenerRelayActor).run(numStep)
      }
    }



case class SimulationActor(roadActors: List[ActorRef[RoadActor.Command]], viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command]):
  private def run(step: Int): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case Step(dt, viewMsgOpt) =>
//          println("[SIMULATION]: Step "+step)
          for viewMsg <- viewMsgOpt do viewListenerRelayActor ! viewMsg
//          println("[SIMULATION]: VIEW UPDATED")
          if step <= 0 then
            simulationEnded
          else {
//          println("SPAWNNNNNN")
          context.spawnAnonymous(
            Aggregator[RoadStepDone, Step](
              sendRequests = replyTo => roadActors.foreach(_ ! RoadActor.Step(dt, replyTo)),
              expectedReplies = roadActors.size,
              replyTo = context.self,
              aggregateReplies = replies =>
                var totalRoads = List[Road]()
                var totalCars = List[Car]()
                var totalTrafficLights = List[TrafficLight]()
                replies.foreach ( reply =>
                    totalRoads = reply.road :: totalRoads
                    totalCars = totalCars.concat(reply.cars)
                    totalTrafficLights = totalTrafficLights.concat(reply.trafficLights)
                )

//                context.system.receptionist ! Receptionist.Find(ViewActor.viewServiceKey, listingResponseAdapter())
//                println(totalCars(1).position)
                Step(dt, List(ViewListenerRelayActor.StepDone(step, totalRoads, totalCars, totalTrafficLights), ViewListenerRelayActor.Stat(computeAverageSpeed(totalCars)))),
              timeout = 5.seconds
            )
          )
          run(step-1)}
      }
    }

  private def simulationEnded: Behavior[Command] =
    viewListenerRelayActor ! ViewListenerRelayActor.SimulationEnded(0) //todo improve time elapsed
    Behaviors.stopped

  private def computeAverageSpeed(agents: List[Car]): Double =
    var avSpeed = .0
    var maxSpeed = -1.0
    var minSpeed = Double.MaxValue
    for (agent <- agents) {
      val car = agent
      val currSpeed = car.speed
      avSpeed = avSpeed + currSpeed
      if (currSpeed > maxSpeed) maxSpeed = currSpeed
      else if (currSpeed < minSpeed) minSpeed = currSpeed
    }
    if (agents.nonEmpty) avSpeed /= agents.size
    avSpeed

enum SimulationType:
  case SINGLE_ROAD_TWO_CAR,
  SINGLE_ROAD_SEVERAL_CARS,
  SINGLE_ROAD_WITH_TRAFFIC_TWO_CAR,
  CROSS_ROADS,
  MASSIVE_SIMULATION;
object SimulationType:
  extension (simulationType: SimulationType) def simulationSetup: List[RoadBuildData] = simulationType match
    case SimulationType.SINGLE_ROAD_TWO_CAR => SimulationExample.trafficSimulationSingleRoadTwoCars
    case SimulationType.SINGLE_ROAD_SEVERAL_CARS => ???
    case SimulationType.SINGLE_ROAD_WITH_TRAFFIC_TWO_CAR => ???
    case SimulationType.CROSS_ROADS => SimulationExample.trafficSimulationWithCrossRoads
    case SimulationType.MASSIVE_SIMULATION => SimulationExample.trafficSimulationMassiveTest


trait SimulationListener:
  def notifyInit(t: Int, agents: List[Car]): Unit
  def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit
  def notifySimulationEnded(simulationDuration: Int): Unit
  def notifyStat(averageSpeed: Double): Unit
  def simulationStopped(): Unit

object SimulationExample:
  def trafficSimulationSingleRoadTwoCars: List[RoadBuildData] =
    val road = Road("road-1", Point2D(0,300), Point2D(1500, 300))
    val car1 = BaseCarAgent("car-1", 0, road, CarAgentConfiguration(0.1,0.2,8.0))
    val car2 = BaseCarAgent("car-2", 100, road, CarAgentConfiguration(0.1,0.1,7.0))
    List(RoadBuildData(road, List.empty, List(car1, car2)))

  def trafficSimulationMassiveTest: List[RoadBuildData] =
    val road = Road("road-1", Point2D(0,300), Point2D(15000, 300))
    var cars = List[Car]()
    for i <- 0 until 5000 do
      cars = cars :+ BaseCarAgent("car-"+i, i*10, road, CarAgentConfiguration(1.0,0.3,7.0))
    List(RoadBuildData(road, List.empty, cars))

  def trafficSimulationWithCrossRoads: List[RoadBuildData] =
    val road1 = Road("road-1", Point2D(0,300), Point2D(1500, 300))
    val tl1 = TrafficLight("trafficLight-1", TrafficLightPositionInfo(Point2D(740,200), 740), TrafficLightTimingSetup(75,25,100), TrafficLightState.GREEN)
    val car1 = ExtendedCarAgent("car-1", 0, road1, CarAgentConfiguration(0.1,0.3,6.0))
    val car2 = ExtendedCarAgent("car-2", 100, road1, CarAgentConfiguration(0.1,0.3,5.0))
    val roadBuildData1 = RoadBuildData(road1, List(tl1), List(car1, car2))
    val road2 = Road("road-2", Point2D(750, 0), Point2D(750, 600))
    val tl2 = TrafficLight("trafficLight-2", TrafficLightPositionInfo(Point2D(750, 290), 290), TrafficLightTimingSetup(75, 25, 100), TrafficLightState.RED)
    val car3 = ExtendedCarAgent("car-3", 0, road2, CarAgentConfiguration(0.1, 0.2, 5.0))
    val car4 = ExtendedCarAgent("car-4", 100, road2, CarAgentConfiguration(0.1, 0.1, 4.0))
    val roadBuildData2 = RoadBuildData(road2, List(tl2), List(car3, car4))
    List(roadBuildData1,roadBuildData2)