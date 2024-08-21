import akka.NotUsed
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import logic.{BaseCarAgent, CarAgentConfiguration, Road, TrafficLight, TrafficLightActor, TrafficLightPositionInfo, TrafficLightState, TrafficLightTimingSetup}
import utils.Point2D
import view.{RoadSimView, ViewActor}

object StartSystem:
  sealed trait Command
  private case class ListingResponse(listing: Receptionist.Listing) extends Command
  case object Spawn extends Command

  def apply(): Behavior[Command] =

    Behaviors.setup { context =>
      context.spawnAnonymous(ViewActor(RoadSimView()))
      context.spawnAnonymous(ViewActor(RoadSimView()))
      val listingResponseAdapter = context.messageAdapter[Receptionist.Listing](ListingResponse.apply)



      Behaviors.receiveMessage {
        case ListingResponse(listing) =>
          listing.allServiceInstances(ViewActor.viewServiceKey).foreach { viewActorRef =>
            viewActorRef ! ViewActor.StepDone(0, List.empty,
              List(
                BaseCarAgent("car-1", CarAgentConfiguration(50,1,0,0,Road("road-1", Point2D(0,300), Point2D(1500, 300)))),
                BaseCarAgent("car-2", CarAgentConfiguration(80,2,0,0,Road("road-1", Point2D(0,300), Point2D(1500, 300))))
              ), List.empty)
          }
          Behaviors.same
        case Spawn =>
          context.system.receptionist ! Receptionist.Find(ViewActor.viewServiceKey, listingResponseAdapter)
          Behaviors.same
      }
    }

object StartSystem2:
  sealed trait Command
  private case class ListingResponse(listing: Receptionist.Listing) extends Command
  case object Spawn extends Command

  def apply(): Behavior[Command] =

    Behaviors.setup { context =>
      context.spawnAnonymous(ViewActor(RoadSimView()))
      context.spawnAnonymous(ViewActor(RoadSimView()))
      val listingResponseAdapter = context.messageAdapter[Receptionist.Listing](ListingResponse.apply)



      Behaviors.receiveMessage {
        case ListingResponse(listing) =>
          listing.allServiceInstances(ViewActor.viewServiceKey).foreach { viewActorRef =>
            viewActorRef ! ViewActor.StepDone(0, List.empty,
              List(
                BaseCarAgent("car-1", CarAgentConfiguration(50,1,0,0,Road("road-1", Point2D(0,300), Point2D(1500, 300)))),
                BaseCarAgent("car-2", CarAgentConfiguration(80,2,0,0,Road("road-1", Point2D(0,300), Point2D(1500, 300))))
              ), List.empty)
          }
          Behaviors.same
        case Spawn =>
          context.system.receptionist ! Receptionist.Find(ViewActor.viewServiceKey, listingResponseAdapter)
          Behaviors.same
      }
    }



@main def Main(): Unit =
  val tl = TrafficLight("TrafficLight-1",TrafficLightPositionInfo(Point2D(0,0), 0), TrafficLightTimingSetup(0,0,0), TrafficLightState.RED)

  val sys = ActorSystem(StartSystem(), "root")
  sys ! StartSystem.Spawn
  println("ASS03")