package logic

case class Point2D(x: Double, y: Double)

enum TrafficLightState:
  case GREEN, YELLOW, RED

trait TrafficLight:

  def step(dt: Int): TrafficLight

  def position: Point2D

  def roadPosition: Double

  def isGreen: Boolean

  def isRed: Boolean

  def isYellow: Boolean

object TrafficLight:
  def apply(position: Point2D,
            roadPosition: Double,
            greenDuration: Int,
            yellowDuration: Int,
            redDuration: Int): TrafficLight = ???

  private case class TrafficLightImpl(val position: Point2D,
                                      val roadPosition: Double,
                                      val greenDuration: Int,
                                      val yellowDuration: Int,
                                      val redDuration: Int,
                                      val state: TrafficLightState = TrafficLightState.GREEN,
                                      val timeInState: Int = 0) extends TrafficLight:

    private def updateState(currentState: TrafficLightState, duration: Int, nextState: TrafficLightState): TrafficLight =
      if timeInState >= duration then this.copy(state = nextState, timeInState = 0) else this


    override def step(dt: Int): TrafficLight = state match
      case TrafficLightState.GREEN => updateState(TrafficLightState.GREEN, greenDuration, TrafficLightState.YELLOW)
      case TrafficLightState.YELLOW => updateState(TrafficLightState.YELLOW, yellowDuration, TrafficLightState.RED)
      case TrafficLightState.RED => updateState(TrafficLightState.RED, redDuration, TrafficLightState.GREEN)


    override def isGreen: Boolean = state == TrafficLightState.GREEN

    override def isRed: Boolean = state == TrafficLightState.RED

    override def isYellow: Boolean = state == TrafficLightState.YELLOW


trait Road:
  def startPoint: Point2D

  def endPoint: Point2D

  def addTrafficLight(position: Point2D, initialState: TrafficLightState, greenDuration: Int, yellowDuration: Int, redDuration: Int, roadPosition: Double): Unit

  def trafficLights: List[TrafficLight]

  def length: Double


trait Environment /*extends SimulationComponent*/:

  def step(): Unit
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

