package logic

import utils.*


enum TrafficLightState:
  case GREEN, YELLOW, RED
  
trait TrafficLight:
  def step(dt: Int): TrafficLight
  def position: Point2D
  def roadPosition: Double
  def state: TrafficLightState
  
case class TrafficLightTimingSetup(greenDuration: Int, yellowDuration: Int, redDuration: Int)

object TrafficLight:
  def apply(position: Point2D,
            roadPosition: Double,
            timingSetup: TrafficLightTimingSetup,
            initialState: TrafficLightState): TrafficLight =
      TrafficLightImpl(position, roadPosition, timingSetup, initialState)
  
  private case class TrafficLightImpl(position: Point2D,
                                      roadPosition: Double,
                                      timingSetup: TrafficLightTimingSetup,
                                      state: TrafficLightState,
                                      private val timeInState: Int = 0) extends TrafficLight:

    private def updateState(duration: Int, nextState: TrafficLightState): TrafficLight =
      if timeInState >= duration then this.copy(state = nextState, timeInState = 0) else this
    
    override def step(dt: Int): TrafficLight = state match
      case TrafficLightState.GREEN => updateState(timingSetup.greenDuration, TrafficLightState.YELLOW)
      case TrafficLightState.YELLOW => updateState(timingSetup.yellowDuration, TrafficLightState.RED)
      case TrafficLightState.RED => updateState(timingSetup.redDuration, TrafficLightState.GREEN)

trait Road:
  def startPoint: Point2D
  def endPoint: Point2D
  def addTrafficLight(position: Point2D, initialState: TrafficLightState, timingSetup: TrafficLightTimingSetup, roadPosition: Double): Road
  def trafficLights: List[TrafficLight]
  def length: Double

object Road:
  def apply(startPoint: Point2D, endPoint: Point2D): Road = RoadImpl(startPoint, endPoint)
  private case class RoadImpl(startPoint: Point2D, endPoint: Point2D, trafficLights: List[TrafficLight] = List.empty) extends Road:
    override def addTrafficLight(position: Point2D, initialState: TrafficLightState, timingSetup: TrafficLightTimingSetup, roadPosition: Double): Road =
      this.copy(trafficLights = TrafficLight(position, roadPosition, timingSetup, initialState) :: trafficLights)
    override def length: Double = Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2))
    
    
    
    
    
    
trait Environment:

  def step(): Environment
  //
  //  def registerNewCarAgent(abstractCarAgent: AbstractCarAgent): Unit
  //
  //  def CarAgents(): List[AbstractCarAgent]

  def createRoad(p0: Point2D, p1: Point2D): Road

  def Roads(): List[Road]

  def TrafficLights(): List[TrafficLight]
//
//  def getCurrentPerception(agentID: String): Perception
//
//  def doAction(agentID: String, selectedAction: Action): Unit

