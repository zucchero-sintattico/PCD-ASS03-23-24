package logic

import scala.util.{Random, Try}

trait BaseCarAgentState
case object Stopped extends BaseCarAgentState
case object Accelerating extends BaseCarAgentState
case object MovingConstantSpeed extends BaseCarAgentState
case object DeceleratingBecauseOfACar extends BaseCarAgentState
case object WaitABit extends BaseCarAgentState


trait CarAgentAction
case class MoveForward(distance: Double) extends CarAgentAction


import logic.Environment
class Agent(val agentID: String,
                                var position: Double,
                                val acceleration: Double,
                                val deceleration: Double,
                                val maxSpeed: Double):

  private val CAR_NEAR_DIST = 15
  private val CAR_FAR_ENOUGH_DIST = 20
  private val MAX_WAITING_TIME = 2
  private var waitingTime: Int = 0
  private var currentSpeed: Double = 0
  private var currentPerception= ???
  private var selectedAction: CarAgentAction = MoveForward(0)
  private var stepSize: Int = ???

 // environment.registerNewCarAgent(this)



  private def senseAndDecide()= ???
  private def sense(): Unit =
    currentPerception = ???

  private def doAction(): Unit = ???

  private var state: BaseCarAgentState = Stopped

  private def detectedNearCar: Boolean = ???

  private def carFarEnough: Boolean = ???

  protected def decide(): Unit =
    state match {
      case Stopped if !detectedNearCar =>
        state = Accelerating
      case Accelerating if detectedNearCar =>
        state = DeceleratingBecauseOfACar
      case Accelerating =>
        currentSpeed += acceleration * stepSize
        if (currentSpeed >= maxSpeed) {
          state = MovingConstantSpeed
        }
      case MovingConstantSpeed if detectedNearCar =>
        state = DeceleratingBecauseOfACar
      case DeceleratingBecauseOfACar =>
        currentSpeed -= deceleration * stepSize
        if (currentSpeed <= 0) {
          state = Stopped
        } else if (carFarEnough) {
          state = WaitABit
          waitingTime = 0
        }
      case WaitABit =>
        waitingTime += stepSize.toInt
        if (waitingTime > MAX_WAITING_TIME) {
          state = Accelerating
        }
      case _ =>
    }

    if (currentSpeed > 0)
      selectedAction = MoveForward(currentSpeed * stepSize)

