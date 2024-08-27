package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
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

case class CarPerception(roadPosition: Double, nearestCarInFront: Option[Car], nearestTrafficLightInFront: Option[TrafficLight])

trait Car:
  val carNearDist = 15
  val carFarEnoughDist = 20
  val maxWaitingTime = 2
  val semNearDist = 100
  val agentID: String
  val position: Double
  val road: Road
  val configuration: CarAgentConfiguration
  val selectedAction: Option[Action]
  val speed: Double
  def decide(dt: Int, carPerception: CarPerception): Car
  def updatePositionAndRemoveAction(newPosition: Double): Car
  protected def isNearestCarInFrontDetected(carPerception: CarPerception): Boolean =
    carPerception.nearestCarInFront.isDefined &&
      ((carPerception.nearestCarInFront.get.position - carPerception.roadPosition) < carNearDist)
  protected def isNearestCarInFrontFarEnough(carPerception: CarPerception): Boolean =
    carPerception.nearestCarInFront.isDefined &&
      ((carPerception.nearestCarInFront.get.position - carPerception.roadPosition) > carFarEnoughDist)
  protected def isNearestTrafficLightRedOrYellow(carPerception: CarPerception): Boolean =
    carPerception.nearestTrafficLightInFront.isDefined &&
      carPerception.nearestTrafficLightInFront.get.state != TrafficLightState.GREEN &&
      carPerception.nearestTrafficLightInFront.get.trafficLightPositionInfo.roadPosition - carPerception.roadPosition < semNearDist
  protected def isNearestTrafficLightGreen(carPerception: CarPerception): Boolean =
    carPerception.nearestTrafficLightInFront.isDefined &&
      carPerception.nearestTrafficLightInFront.get.state == TrafficLightState.GREEN

enum BaseCarAgentState:
  case STOPPED, ACCELERATING, DECELERATING_BECAUSE_OF_A_CAR, WAIT_A_BIT, MOVING_CONSTANT_SPEED

case class BaseCarAgent(agentID: String, position: Double, road: Road, configuration: CarAgentConfiguration, selectedAction: Option[Action] = None, speed: Double = 0, private val state: BaseCarAgentState = BaseCarAgentState.STOPPED, private val waitingTime: Int = 0) extends Car:
  override def decide(dt: Int, carPerception: CarPerception): Car =
    val detectedNearCar = isNearestCarInFrontDetected(carPerception)
    val carFarEnough = isNearestCarInFrontFarEnough(carPerception)
    var car = this
    state match
      case BaseCarAgentState.STOPPED => if !detectedNearCar then car = car.copy(state = BaseCarAgentState.ACCELERATING)
      case BaseCarAgentState.ACCELERATING =>
        if detectedNearCar then
          car = car.copy(state = BaseCarAgentState.DECELERATING_BECAUSE_OF_A_CAR)
        else
          val newSpeed = speed + configuration.acceleration * dt
          val newState = if newSpeed >= configuration.maxSpeed then BaseCarAgentState.MOVING_CONSTANT_SPEED else BaseCarAgentState.ACCELERATING
          car = car.copy(speed = newSpeed, state = newState)
      case BaseCarAgentState.DECELERATING_BECAUSE_OF_A_CAR =>
        val newSpeed = speed - configuration.deceleration * dt
        car = car.copy(speed = newSpeed)
        if newSpeed <= 0 then car = car.copy(state = BaseCarAgentState.STOPPED)
        else if carFarEnough then car = car.copy(waitingTime = 0, state = BaseCarAgentState.WAIT_A_BIT)
      case BaseCarAgentState.WAIT_A_BIT =>
        val newWaitingTime = waitingTime + dt
        car = car.copy(waitingTime = newWaitingTime)
        if newWaitingTime > maxWaitingTime then car = car.copy(state = BaseCarAgentState.ACCELERATING)
      case BaseCarAgentState.MOVING_CONSTANT_SPEED => if detectedNearCar then car = car.copy(state = BaseCarAgentState.DECELERATING_BECAUSE_OF_A_CAR)
    if car.speed > 0 then car.copy(selectedAction = Option(MoveForward(car.speed * dt))) else car

  override def updatePositionAndRemoveAction(newPosition: Double): Car = copy(position = newPosition, selectedAction = Option.empty)

enum ExtendedCarAgentState:
  case STOPPED, ACCELERATING, DECELERATING_BECAUSE_OF_A_CAR, DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM, WAITING_FOR_GREEN_SEM, WAIT_A_BIT, MOVING_CONSTANT_SPEED

case class ExtendedCarAgent(agentID: String, position: Double, road: Road, configuration: CarAgentConfiguration, selectedAction: Option[Action] = None, speed: Double = 0, private val state: ExtendedCarAgentState = ExtendedCarAgentState.STOPPED, private val waitingTime: Int = 0) extends Car:
  override def decide(dt: Int, carPerception: CarPerception): Car =
    val detectedNearCar = isNearestCarInFrontDetected(carPerception)
    val carFarEnough = isNearestCarInFrontFarEnough(carPerception)
    val detectedRedOrYellowNearTrafficLights = isNearestTrafficLightRedOrYellow(carPerception)
    val detectedGreenTrafficLights = isNearestTrafficLightGreen(carPerception)
    var car = this
    state match
      case ExtendedCarAgentState.STOPPED => if !detectedNearCar then car = car.copy(state = ExtendedCarAgentState.ACCELERATING)
      case ExtendedCarAgentState.ACCELERATING =>
        if detectedNearCar then
          car = car.copy(state = ExtendedCarAgentState.DECELERATING_BECAUSE_OF_A_CAR)
        else if detectedRedOrYellowNearTrafficLights then
          car = car.copy(state = ExtendedCarAgentState.DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM)
        else
          val newSpeed = speed + configuration.acceleration * dt
          val newState = if newSpeed >= configuration.maxSpeed then ExtendedCarAgentState.MOVING_CONSTANT_SPEED else ExtendedCarAgentState.ACCELERATING
          car = car.copy(speed = newSpeed, state = newState)
      case ExtendedCarAgentState.MOVING_CONSTANT_SPEED =>
        if detectedNearCar then car = car.copy(state = ExtendedCarAgentState.DECELERATING_BECAUSE_OF_A_CAR)
        else if detectedRedOrYellowNearTrafficLights then car = car.copy(state = ExtendedCarAgentState.DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM)
      case ExtendedCarAgentState.DECELERATING_BECAUSE_OF_A_CAR =>
        val newSpeed = speed - configuration.deceleration * dt
        car = car.copy(speed = newSpeed)
        if newSpeed <= 0 then car = car.copy(state = ExtendedCarAgentState.STOPPED)
        else if carFarEnough then car = car.copy(waitingTime = 0, state = ExtendedCarAgentState.WAIT_A_BIT)
      case ExtendedCarAgentState.DECELERATING_BECAUSE_OF_A_NOT_GREEN_SEM =>
        val newSpeed = speed - configuration.deceleration * dt
        car = car.copy(speed = newSpeed)
        if newSpeed <= 0 then car = car.copy(state = ExtendedCarAgentState.WAITING_FOR_GREEN_SEM)
        else if !detectedRedOrYellowNearTrafficLights then car = car.copy(state = ExtendedCarAgentState.ACCELERATING)
      case ExtendedCarAgentState.WAIT_A_BIT =>
        val newWaitingTime = waitingTime + dt
        car = car.copy(waitingTime = newWaitingTime)
        if newWaitingTime > maxWaitingTime then car = car.copy(state = ExtendedCarAgentState.ACCELERATING)
      case ExtendedCarAgentState.WAITING_FOR_GREEN_SEM =>
        if detectedGreenTrafficLights then car = car.copy(state = ExtendedCarAgentState.ACCELERATING)
    if car.speed > 0 then car.copy(selectedAction = Option(MoveForward(car.speed * dt))) else car

  override def updatePositionAndRemoveAction(newPosition: Double): Car = copy(position = newPosition, selectedAction = Option.empty)