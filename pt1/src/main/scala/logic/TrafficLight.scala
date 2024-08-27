package logic

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import utils.*

object TrafficLightActor:
  sealed trait Command
  final case class Step(dt: Int, replyTo: ActorRef[RoadActor.TrafficLightStepDone.type]) extends Command
  final case class RequestTrafficLightRecord(replyTo: ActorRef[RoadActor.TrafficLightRecord]) extends Command

  def apply(trafficLight: TrafficLight): Behavior[Command] =
    Behaviors.receiveMessage {
        case Step(dt, replyTo) =>
          replyTo ! RoadActor.TrafficLightStepDone
          TrafficLightActor(trafficLight.step(dt))
        case RequestTrafficLightRecord(replyTo) =>
          replyTo ! RoadActor.TrafficLightRecord(trafficLight)
          Behaviors.same
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
    if updatedTimeInState >= duration then copy(state = nextState, timeInState = 0) else copy(timeInState = updatedTimeInState)

  def step(dt: Int): TrafficLight = state match
    case TrafficLightState.GREEN => updateState(dt, timingSetup.greenDuration, TrafficLightState.YELLOW)
    case TrafficLightState.YELLOW => updateState(dt, timingSetup.yellowDuration, TrafficLightState.RED)
    case TrafficLightState.RED => updateState(dt, timingSetup.redDuration, TrafficLightState.GREEN)