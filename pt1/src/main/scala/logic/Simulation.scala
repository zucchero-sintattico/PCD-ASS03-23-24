package logic

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.SimulationActor.{Command, ListingResponse, RoadStepDone, Step}
import utils.Point2D
import view.ViewListenerRelayActor

import scala.concurrent.duration.DurationInt

object SimulationActor:
  sealed trait Command
  case object Start extends Command
  case object Stop extends Command
  private case class Step(dt: Int, viewMsg: Option[ViewListenerRelayActor.Command] = Option.empty) extends Command
  final case class RoadStepDone(road: Road, cars: List[Car], trafficLights: List[TrafficLight]) extends Command
  private case class ListingResponse(viewMessage: ViewListenerRelayActor.Command, listing: Receptionist.Listing) extends Command



  def apply(dt: Int, numStep: Int, roadsBuildData: List[RoadBuildData], viewListenerRelayActor: ActorRef[ViewListenerRelayActor.Command]): Behavior[Command] =
    Behaviors.setup { context =>
      val roadActors = roadsBuildData.map(rbd => context.spawn(RoadActor(rbd.road, rbd.trafficLights, rbd.cars), rbd.road.agentID))
      Behaviors.receiveMessage{
        case Start =>
          context.self ! Step(dt)
          SimulationActor(roadActors).run(numStep)
      }

    }

case class SimulationActor(roadActors: List[ActorRef[RoadActor.Command]]):
  private def run(step: Int): Behavior[Command] =
    Behaviors.setup { context =>

      val listingResponseAdapter = (msg: ViewListenerRelayActor.Command) => context.messageAdapter[Receptionist.Listing](ListingResponse(msg, _))
      Behaviors.receiveMessage {
        case Step(dt, viewMsgOpt) =>
          println("[SIMULATION]: Step "+step)
          for viewMsg <- viewMsgOpt do context.system.receptionist ! Receptionist.Find(ViewListenerRelayActor.viewServiceKey, listingResponseAdapter(viewMsg))
          println("[SIMULATION]: VIEW UPDATED")
          if step <= 0 then
//            println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaahjHADSJLDHDKJHASKDHASKJH DACS")
            endSimulation
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
                println(totalCars(1).position)
                Step(dt, Option(ViewListenerRelayActor.StepDone(step, totalRoads, totalCars, totalTrafficLights))),
              timeout = 5.seconds
            )
          )
          run(step-1)}
        case ListingResponse(viewMessage, listing) =>
          println("receptionistOK")
          listing.allServiceInstances(ViewListenerRelayActor.viewServiceKey).foreach { viewActorRef =>
            println("sending")
            viewActorRef ! viewMessage
        }
        Behaviors.same
      }

    }
  private def endSimulation: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {

        case Step(dt, viewMsg) =>
//          println("AAAAAAAAAAAAAAAAAa")
          Behaviors.same

        case ListingResponse(viewMessage, listing) =>
//          println("receptionistOK")
          listing.allServiceInstances(ViewListenerRelayActor.viewServiceKey).foreach { viewActorRef =>
            viewActorRef ! viewMessage
          }
          Behaviors.same
      }
    }


enum SimulationType:
  case SINGLE_ROAD_TWO_CAR,
  SINGLE_ROAD_SEVERAL_CARS,
  SINGLE_ROAD_WITH_TRAFFIC_TWO_CAR,
  CROSS_ROADS,
  MASSIVE_SIMULATION;

  def getSimulation(simulationType: SimulationType): SimulationActor = simulationType match
    case SimulationType.SINGLE_ROAD_TWO_CAR => ???
    case SimulationType.SINGLE_ROAD_SEVERAL_CARS => ???
    case SimulationType.SINGLE_ROAD_WITH_TRAFFIC_TWO_CAR => ???
    case SimulationType.CROSS_ROADS => ???
    case SimulationType.MASSIVE_SIMULATION => ???


trait SimulationListener:
  def notifyInit(t: Int, agents: List[Car]): Unit
  def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit
  def notifySimulationEnded(simulationDuration: Int): Unit
  def notifyStat(averageSpeed: Double): Unit
  def simulationStopped(): Unit

object SimulationExample:
  def trafficSimulationSingleRoadTwoCars: List[RoadBuildData] =
    val road = Road("road-1", Point2D(0,300), Point2D(1500, 300))
    val car1 = BaseCarAgent("car-1", 0, road, CarAgentConfiguration(0.1,0.2,8))
    val car2 = BaseCarAgent("car-2", 100, road, CarAgentConfiguration(0.1,0.1,7))
    List(RoadBuildData(road, List.empty, List(car1, car2)))
