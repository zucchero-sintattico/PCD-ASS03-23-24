package utils

import logic.{Car, Road, SimulationListener, TrafficLight}

import java.nio.file.{Files, Paths}
import java.io.FileWriter

class RoadSimStatistics extends SimulationListener:
  private var _averageSpeed = .0
  private val _minSpeed = .0
  private val _maxSpeed = .0
  private val path = Paths.get("log.txt")

  override def notifyInit(t: Int, agents: List[Car]): Unit =
    if Files.exists(path) then Files.delete(path)
    _averageSpeed = 0

  override def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit = {}

  override def notifySimulationEnded(simulationDuration: Int): Unit = {}

  override def notifyStat(averageSpeed: Double): Unit = log("average speed: " + averageSpeed)

  override def simulationStopped(): Unit = {}

  def averageSpeed: Double = _averageSpeed

  def minSpeed: Double = _minSpeed

  def maxSpeed: Double = _maxSpeed

  private def log(msg: String): Unit =
    System.out.println("[STAT] " + msg)
    //save to file
    //delete file if exists
    try {
      val fw = new FileWriter("log.txt", true)
      fw.write(msg + "\n")
      fw.close()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }




