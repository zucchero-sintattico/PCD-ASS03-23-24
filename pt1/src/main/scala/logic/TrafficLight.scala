package logic

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import logic.Hal.Response
import utils.*

object TrafficLightActor:
  
  sealed trait Command
  final case class Step(dt: Int) extends Command
  
  def apply(trafficLight: TrafficLight): Behavior[Command] =
    Behaviors.receive { (context, message) => message match
        case Step(dt) =>
          TrafficLightActor(trafficLight.step(dt))
    }

enum TrafficLightState:
  case GREEN, YELLOW, RED

case class TrafficLightTimingSetup(greenDuration: Int, yellowDuration: Int, redDuration: Int)

case class TrafficLightPositionInfo(position: Point2D, roadPosition: Double)

case class TrafficLight(agentID: String,
                        trafficLightPositionInfo: TrafficLightPositionInfo,
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
