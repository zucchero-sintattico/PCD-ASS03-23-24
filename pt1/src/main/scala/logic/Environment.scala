package logic

import utils.*


enum TrafficLightState:
  case GREEN, YELLOW, RED

case class TrafficLightTimingSetup(greenDuration: Int, yellowDuration: Int, redDuration: Int)

case class TrafficLight(position: Point2D,
                        roadPosition: Double,
                        timingSetup: TrafficLightTimingSetup,
                        state: TrafficLightState,
                        private val timeInState: Int = 0):

  private def updateState(duration: Int, nextState: TrafficLightState): TrafficLight =
    if timeInState >= duration then this.copy(state = nextState, timeInState = 0) else this

  def step(dt: Int): TrafficLight = state match
    case TrafficLightState.GREEN => updateState(timingSetup.greenDuration, TrafficLightState.YELLOW)
    case TrafficLightState.YELLOW => updateState(timingSetup.yellowDuration, TrafficLightState.RED)
    case TrafficLightState.RED => updateState(timingSetup.redDuration, TrafficLightState.GREEN)


case class Road(startPoint: Point2D, endPoint: Point2D, trafficLights: List[TrafficLight] = List.empty):
  def addTrafficLight(position: Point2D, initialState: TrafficLightState, timingSetup: TrafficLightTimingSetup, roadPosition: Double): Road =
    this.copy(trafficLights = TrafficLight(position, roadPosition, timingSetup, initialState) :: trafficLights)
  def length: Double = Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2))

class Environment:
  private var _roads: List[Road] = List.empty
  private var _trafficLights: List[TrafficLight] = List.empty

  def step(dt: Int): Unit = _trafficLights = _trafficLights.map(_.step(dt))


  def createRoad(p0: Point2D, p1: Point2D): Road =
    val road = Road(p0, p1)
    _roads = road :: _roads
    road

  def roads: List[Road] = roads

  def trafficLights: List[TrafficLight] = trafficLights




//trait Environment:
//
//  def step(): Environment
//  //
//  //  def registerNewCarAgent(abstractCarAgent: AbstractCarAgent): Unit
//  //
//  //  def CarAgents(): List[AbstractCarAgent]
//
//  def createRoad(p0: Point2D, p1: Point2D): Road
//
//  def Roads(): List[Road]
//
//  def TrafficLights(): List[TrafficLight]
////
////  def getCurrentPerception(agentID: String): Perception
////
////  def doAction(agentID: String, selectedAction: Action): Unit
//
