import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import logic.{BaseCarAgent, CarAgentConfiguration, Road, TrafficLight, TrafficLightActor, TrafficLightPositionInfo, TrafficLightState, TrafficLightTimingSetup}
import utils.Point2D
import view.{RoadSimView, ViewActor}

object StartSystem:
  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val view = RoadSimView()
      view.display()
      val viewActor = context.spawn(ViewActor(view), "view")
      viewActor ! ViewActor.StepDone(0, List.empty,
        List(
          BaseCarAgent("car-1", CarAgentConfiguration(50,1,0,0,Road("road-1", Point2D(0,300), Point2D(1500, 300)))),
          BaseCarAgent("car-2", CarAgentConfiguration(80,2,0,0,Road("road-1", Point2D(0,300), Point2D(1500, 300))))
        ), List.empty)
      Behaviors.same
    }


@main def Main(): Unit =
  val tl = TrafficLight("TrafficLight-1",TrafficLightPositionInfo(Point2D(0,0), 0), TrafficLightTimingSetup(0,0,0), TrafficLightState.RED)

  ActorSystem(StartSystem(), "root")
  println("ASS03")