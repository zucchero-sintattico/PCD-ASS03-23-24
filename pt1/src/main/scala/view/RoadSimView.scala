package view

import javax.swing.WindowConstants
import logic.{Car, Road, TrafficLight, TrafficLightState}
import utils.Vector2D

import java.awt.{BorderLayout, Color, Graphics, Graphics2D, RenderingHints}
import javax.swing.*

object RoadSimView:
  val carDrawSize = 10

case class RoadSimView() extends JFrame("RoadSim View") with DisposableSimulationListener:
  private val panel: RoadSimViewPanel = RoadSimViewPanel(1500, 600)
  setSize(1500, 600)
  panel.setSize(1500, 600)
  val cp = new JPanel
  cp.setLayout(new BorderLayout)
  cp.add(BorderLayout.CENTER, panel)
  setContentPane(cp)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  display()

  def display(): Unit = SwingUtilities.invokeLater(() => setVisible(true))

  override def notifyInit(t: Int, agents: List[Car]): Unit = {}

  override def notifyStepDone(t: Int, roads: List[Road], cars: List[Car], tl: List[TrafficLight]): Unit = 
    SwingUtilities.invokeLater(() => panel.update(roads, cars, tl))

  override def notifySimulationEnded(simulationDuration: Int): Unit = {}

  override def notifyStat(averageSpeed: Double): Unit = {}

  private[view] case class RoadSimViewPanel(w: Int, h: Int) extends JPanel:
    private[view] var cars: List[Car] = List()
    private[view] var roads: List[Road] = List()
    private[view] var trafficLights: List[TrafficLight] = List()

    override def paintComponent(g: Graphics): Unit =
      super.paintComponent(g)
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.clearRect(0, 0, this.getWidth, this.getHeight)
      for r <- roads do
        g2.drawLine(r.startPoint.x.toInt, r.startPoint.y.toInt, r.endPoint.x.toInt, r.endPoint.y.toInt)
      for tl <- trafficLights do
        tl.state match
          case TrafficLightState.GREEN => g.setColor(Color.GREEN)
          case TrafficLightState.YELLOW => g.setColor(Color.YELLOW)
          case TrafficLightState.RED => g.setColor(Color.RED)
        g2.fillRect((tl.trafficLightPositionInfo.position.x-5).toInt, (tl.trafficLightPositionInfo.position.y-5).toInt, 10, 10);
      for c <- cars do
        val pos = c.position
        val r = c.road
        val dir = Vector2D.makeV2d(r.startPoint, r.endPoint).getNormalized.mul(pos)
        g2.fillOval((r.startPoint.x + dir.x - RoadSimView.carDrawSize / 2).toInt, (r.startPoint.y + dir.y - RoadSimView.carDrawSize / 2).toInt, RoadSimView.carDrawSize, RoadSimView.carDrawSize)

    def update(roads: List[Road], cars: List[Car], trafficLights: List[TrafficLight]): Unit =
      this.roads = roads
      this.cars = cars
      this.trafficLights = trafficLights
      repaint()





