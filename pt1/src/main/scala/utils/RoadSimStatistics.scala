package utils

import logic.{Car, Road, SimulationListener, TrafficLight}

import java.nio.file.{Files, Paths}
import java.io.FileWriter

class RoadSimStatistics extends SimulationListener:
  private var _averageSpeed = .0
  private val path = Paths.get("log.txt")

  override def notifyInit(t: Int, agents: List[Car]): Unit =
    if Files.exists(path) then Files.delete(path)
    _averageSpeed = 0

  override def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit = {}

  override def notifySimulationEnded(simulationDuration: Int): Unit = {}

  override def notifyStat(averageSpeed: Double): Unit = log("average speed: " + averageSpeed)

  def averageSpeed: Double = _averageSpeed

  private def log(msg: String): Unit =
//    println("[RoadSimStatistics]: log step stats")
    try
      val fw = new FileWriter(path.toFile, true)
      try
        fw.write(msg + "\n")
      finally
        fw.close()
    catch
      case e: Exception => e.printStackTrace()






