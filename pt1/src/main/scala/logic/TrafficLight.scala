package logic

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import utils.*

sealed trait PositionInfo //temporary
final case class trafficLightPositionInfo(trafficLightPositionInfo: TrafficLightPositionInfo) extends PositionInfo

sealed trait Command
final case class step(dt: Int) extends Command
final case class getPositionInfo(replyTo: ActorRef[PositionInfo]) extends Command

enum TrafficLightState:
  case GREEN, YELLOW, RED

case class TrafficLightTimingSetup(greenDuration: Int, yellowDuration: Int, redDuration: Int)

case class TrafficLightPositionInfo(position: Point2D, roadPosition: Double)

object TrafficLightActor:
  def apply(trafficLight: TrafficLight): Behavior[Command] =
    Behaviors.receive { (context, message) => message match
        case step(dt) =>
          TrafficLightActor(trafficLight.step(dt))
        case getPositionInfo(replyTo) =>
          replyTo ! trafficLightPositionInfo(trafficLight.trafficLightPositionInfo)
          Behaviors.same
    }


//alternative implementation
//object TrafficLightActor:
//  def apply(trafficLightPositionInfo: TrafficLightPositionInfo,
//            timingSetup: TrafficLightTimingSetup,
//            state: TrafficLightState): Behavior[Command] =
//    Behaviors.setup { context =>
//      var trafficLight = TrafficLight(trafficLightPositionInfo, timingSetup, state, 0)
//      Behaviors.receiveMessage {
//        case step(dt) =>
//          trafficLight = trafficLight.step(dt)
//          Behaviors.same
//        case getPositionInfo(replyTo) =>
//          replyTo ! new trafficLightPositionInfo(trafficLight.trafficLightPositionInfo)
//          Behaviors.same
//      }
//    }

case class TrafficLight(trafficLightPositionInfo: TrafficLightPositionInfo,
                        timingSetup: TrafficLightTimingSetup,
                        state: TrafficLightState,
                        private val timeInState: Int = 0):

  private def updateState(dt: Int, duration: Int, nextState: TrafficLightState): TrafficLight =
    val updatedTimeInState = timeInState + dt
    if updatedTimeInState >= duration then this.copy(state = nextState, timeInState = 0) else this.copy(timeInState = updatedTimeInState)

  def step(dt: Int): TrafficLight = state match
    case TrafficLightState.GREEN => updateState(dt, timingSetup.greenDuration, TrafficLightState.YELLOW)
    case TrafficLightState.YELLOW => updateState(dt, timingSetup.yellowDuration, TrafficLightState.RED)
    case TrafficLightState.RED => updateState(dt, timingSetup.redDuration, TrafficLightState.GREEN)
