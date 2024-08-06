package view

import javax.swing.WindowConstants
import logic.{BaseCarAgentState, Car, Road, SimulationListener, TrafficLight}
import utils.Vector2D

import java.awt.{BorderLayout, Color, Graphics, Graphics2D, RenderingHints}
import javax.swing.*
import java.util

object RoadSimView {
  private val CAR_DRAW_SIZE = 10
}

class RoadSimView extends JFrame("RoadSim View") with SimulationListener {
  private val panel: RoadSimViewPanel = new RoadSimViewPanel(1500, 600)
  setSize(1500, 600)
  panel.setSize(1500, 600)
  val cp = new JPanel
  cp.setLayout(new BorderLayout)
  cp.add(BorderLayout.CENTER, panel)
  setContentPane(cp)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  display()


  def display(): Unit = {
    SwingUtilities.invokeLater(() => {
      this.setVisible(true)

    })
  }

  def notifyInit(t: Int, agents: List[Car]): Unit = {
  }

  def notifyStepDone(t: Int, roads: List[Road], cars: List[Car], tl: List[TrafficLight]): Unit = {
    System.out.println("NOTIFY STEP DONE" + cars(1).position)
    panel.update(roads, cars, tl)
  }

  private[view] class RoadSimViewPanel(w: Int, h: Int) extends JPanel {
    private[view] var cars: List[Car] = null
    private[view] var roads: List[Road] = null
    private[view] var sems: List[TrafficLight] = null

    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.clearRect(0, 0, this.getWidth, this.getHeight)
      if (roads != null) {
//        import scala.collection.JavaConversions._
        for (r <- roads) {
          g2.drawLine(r.startPoint.x.toInt, r.startPoint.y.toInt, r.endPoint.x.toInt, r.endPoint.y.toInt)
        }
      }
      //            if (sems != null) {
      //                for (var s: sems) {
      //                    if (s.isGreen()) {
      //                        g.setColor(new Color(0, 255, 0, 255));
      //                    } else if (s.isRed()) {
      //                        g.setColor(new Color(255, 0, 0, 255));
      //                    } else {
      //                        g.setColor(new Color(255, 255, 0, 255));
      //                    }
      //                    g2.fillRect((int)(s.getPosition().x()-5), (int)(s.getPosition().y()-5), 10, 10);
      //                }
      //            }
      var i = 0
      val c1 = new Color(255, 0, 0)
      val c2 = new Color(0, 255, 0)
      //g.setColor(new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)));
      BaseCarAgentState.values
      System.out.println("PAINTING")
      if (cars != null) {
//        import scala.collection.JavaConversions._
        for (c <- cars) {
          val pos = c.position
          val r = c.road
          val dir = Vector2D.makeV2d(r.startPoint, r.endPoint).getNormalized.mul(pos)
          //                    g.setColor(c2);
          if (i == 0) {
            //                        g.setColor(c1);
            i += 1
          }
          g2.fillOval((r.startPoint.x + dir.x - RoadSimView.CAR_DRAW_SIZE / 2).toInt, (r.startPoint.y + dir.y - RoadSimView.CAR_DRAW_SIZE / 2).toInt, RoadSimView.CAR_DRAW_SIZE, RoadSimView.CAR_DRAW_SIZE)
        }
      }
    }

    def update(roads: List[Road], cars: List[Car], sems: List[TrafficLight]): Unit = {
      this.roads = roads
      this.cars = cars
      this.sems = sems
      repaint()
    }
  }

  def notifySimulationEnded(simulationDuration: Int): Unit = {
  }

  def notifyStat(averageSpeed: Double): Unit = {
  }
}
