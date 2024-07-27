package logic

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.SimulationActor.{Command, ListingResponse, RoadStepDone, Step}
import view.ViewActor

import scala.concurrent.duration.DurationInt

object SimulationActor:
  sealed trait Command
  case object Start extends Command
  private case class Step(dt: Int, viewMsg: Option[ViewActor.Command] = Option.empty) extends Command
  final case class RoadStepDone(road: Road, cars: List[Car], trafficLights: List[TrafficLight]) extends Command
  private case class ListingResponse(viewMessage: ViewActor.Command, listing: Receptionist.Listing) extends Command



  def apply(dt: Int, numStep: Int, roadsBuildData: List[RoadBuildData]): Behavior[Command] =
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

      val listingResponseAdapter = (msg: ViewActor.Command) => context.messageAdapter[Receptionist.Listing](ListingResponse(msg, _))
      Behaviors.receiveMessage {
        case Step(dt, viewMsgOpt) =>
          println("[SIMULATION]: Step "+step)
          for viewMsg <- viewMsgOpt do context.system.receptionist ! Receptionist.Find(ViewActor.viewServiceKey, listingResponseAdapter(viewMsg))
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
                Step(dt, Option(ViewActor.StepDone(step, totalRoads, totalCars, totalTrafficLights))),
              timeout = 5.seconds
            )
          )
          run(step-1)}
        case ListingResponse(viewMessage, listing) =>
          println("receptionistOK")
          listing.allServiceInstances(ViewActor.viewServiceKey).foreach { viewActorRef =>
//            println("sending")
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
          listing.allServiceInstances(ViewActor.viewServiceKey).foreach { viewActorRef =>
            viewActorRef ! viewMessage
          }
          Behaviors.same
      }
    }



