package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.BaseCarAgentState.{ACCELERATING, DECELERATING_BECAUSE_OF_A_CAR, MOVING_CONSTANT_SPEED, STOPPED, WAIT_A_BIT}
import logic.RoadActor.CarRecord

object CarActor:

  sealed trait Command
  final case class RequestCarRecord(replyTo: ActorRef[RoadActor.CarRecord]) extends Command
  final case class DecideAction(dt: Int, carPerception: CarPerception, replyTo: ActorRef[RoadActor.CarRecord]) extends Command
  final case class UpdateCarRecord(car: Car, replyTo: ActorRef[RoadActor.CarStepDone]) extends Command
  
  def apply(car: Car): Behavior[Command] =
    Behaviors.receive { (context, message) => message match
      case RequestCarRecord(replyTo) =>
        replyTo ! RoadActor.CarRecord(car, context.self)
        Behaviors.same
      case DecideAction(dt, carPerception, replyTo) =>
        replyTo ! RoadActor.CarRecord(car.decide(dt, carPerception), context.self)
        Behaviors.same
      case UpdateCarRecord(car, replyTo) =>
        replyTo ! RoadActor.CarStepDone(car)
        CarActor(car)

    }

sealed trait Action
case class MoveForward(distance: Double) extends Action

case class CarAgentConfiguration(acceleration: Double, deceleration: Double, maxSpeed: Double)

case class CarPerception(roadPosition: Double, nearestCarInFront: Option[Car], nearestTrafficLight: Option[TrafficLight])

enum BaseCarAgentState:
  case STOPPED, ACCELERATING, DECELERATING_BECAUSE_OF_A_CAR, WAIT_A_BIT, MOVING_CONSTANT_SPEED

trait Car:
  val carNearDist = 15
  val carFarEnoughDist = 20
  val maxWaitingTime = 2
  val agentID: String
  val position: Double
  val road: Road
  val configuration: CarAgentConfiguration
  val selectedAction: Option[Action]
  def decide(dt: Int, carPerception: CarPerception): Car
  def updatePositionAndRemoveAction(newPosition: Double): Car
case class BaseCarAgent(agentID: String, position: Double, road: Road, configuration: CarAgentConfiguration, selectedAction: Option[Action] = None, private val state: BaseCarAgentState = STOPPED, private val speed: Double = 0, private val waitingTime: Int = 0) extends Car:
  override def decide(dt: Int, carPerception: CarPerception): Car =
    val detectedNearCar = carPerception.nearestCarInFront.isDefined && ((carPerception.nearestCarInFront.get.position - carPerception.roadPosition) < carNearDist)
    val carFarEnough = carPerception.nearestCarInFront.isDefined && ((carPerception.nearestCarInFront.get.position - carPerception.roadPosition) > carFarEnoughDist)
    var car = this
    state match
      case BaseCarAgentState.STOPPED => if !detectedNearCar then car = car.copy(state = ACCELERATING)
      case BaseCarAgentState.ACCELERATING =>
        if detectedNearCar then
          car = car.copy(state = DECELERATING_BECAUSE_OF_A_CAR)
        else
          val newSpeed = speed + configuration.acceleration * dt
          val newState = if newSpeed >= configuration.maxSpeed then MOVING_CONSTANT_SPEED else ACCELERATING
          car = car.copy(speed = newSpeed, state = state)
      case BaseCarAgentState.DECELERATING_BECAUSE_OF_A_CAR =>
        val newSpeed = speed - configuration.deceleration * dt
        if newSpeed < 0 then car = car.copy(speed = newSpeed, state = STOPPED)
        else if carFarEnough then car = car.copy(speed = newSpeed, state = WAIT_A_BIT)
      case BaseCarAgentState.WAIT_A_BIT =>
        val newWaitingTime = waitingTime + dt
        if newWaitingTime >= maxWaitingTime
        then car = car.copy(state = ACCELERATING)
        else car = car.copy(waitingTime = newWaitingTime)
      case BaseCarAgentState.MOVING_CONSTANT_SPEED => if detectedNearCar then car.copy(state = DECELERATING_BECAUSE_OF_A_CAR)
    println(car.speed)
    if car.speed > 0 then car.copy(selectedAction = Option(MoveForward(car.speed * dt))) else car
  override def updatePositionAndRemoveAction(newPosition: Double): Car = this.copy(position = newPosition, selectedAction = Option.empty)