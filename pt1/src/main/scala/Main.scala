import akka.actor.typed.ActorSystem
import logic.{TrafficLight, TrafficLightActor, TrafficLightPositionInfo, TrafficLightState, TrafficLightTimingSetup}
import utils.Point2D



@main def Main(): Unit =
  val tl = TrafficLight("TrafficLight-1",TrafficLightPositionInfo(Point2D(0,0), 0), TrafficLightTimingSetup(0,0,0), TrafficLightState.RED)
  ActorSystem(TrafficLightActor(tl), "TrafficLightActor")
  println("ASS03")