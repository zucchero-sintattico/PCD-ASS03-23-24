package view

import javax.swing.WindowConstants
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Insets
import java.awt.GridBagConstraints
import java.awt.FlowLayout
import java.awt.Font
import java.util
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.JCheckBox
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities
import logic._
import logic.SimulationType

case class StatisticalView() extends JFrame with SimulationListener with Clickable:
  private val defaultSize = 1000
  private val bigFont = new Font(getName, Font.PLAIN, 16)
  private val smallFont = new Font(getName, Font.PLAIN, 14)
  private val labelNumberOfSteps = new JLabel("Number of steps")
  private val fieldNumberOfSteps = new JTextField("100", 1)
  private val labelConsoleLog = new JLabel("Console log")
  private val areaConsoleLog = new JTextArea("Console log")
  private val buttonStart = new JButton("Start simulation")
  private val buttonReset = new JButton("Reset simulation")
  private val buttonStop = new JButton("Stop simulation")
  private val scroll = new JScrollPane(areaConsoleLog)
  private val labelBox = new JLabel("Choose simulation")
  private val comboBox = new JComboBox[SimulationType]
  private val checkBox = new JCheckBox("Display simulation view")
  private val inputContainer = new JPanel(new GridLayout(2, 2, 10, 10))
  private val buttonContainer = new JPanel(new FlowLayout)
  
  private var simulationStarted = false
  
  setFrameProperties()
  setViewComponents()
  addAllComponentsIntoFrame()
  editAllComponentsProperties()
  pack()

  override def whenClicked(clickMessage: ViewClickRelayActor.Command => Unit): Unit =
    SwingUtilities.invokeLater(() =>
      buttonStart.addActionListener(_ =>
        if !simulationStarted then if validateInput then
          updateViewWhenSimulationStart()
          clearTextArea()
          clickMessage(ViewClickRelayActor.SetupSimulation(simulationType, numberOfSteps.get, showView))
          clickMessage(ViewClickRelayActor.StartSimulation)
        else
          updateViewWhenSimulationStart()
          clickMessage(ViewClickRelayActor.StartSimulation)
      )
      buttonReset.addActionListener(_ =>
        if validateInput then
          clearTextArea()
          areaConsoleLog.setText("Console log")
          resetView()
          clickMessage(ViewClickRelayActor.SetupSimulation(simulationType, numberOfSteps.get, showView))
      )
      buttonStop.addActionListener(_ =>
        buttonStart.setEnabled(true)
        buttonStop.setEnabled(false)
        buttonReset.setEnabled(true)
        clickMessage(ViewClickRelayActor.StopSimulation)
      )
    )

  override def notifyInit(t: Int, agents: List[Car]): Unit =
    SwingUtilities.invokeLater(() => updateView("[Simulation]: START simulation"))

  override def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit =
    SwingUtilities.invokeLater(() => updateView("[STAT] Steps: " + t))

  override def notifySimulationEnded(simulationDuration: Int): Unit =
    SwingUtilities.invokeLater(() =>
      resetView()
      updateView("[SIMULATION] Time: " + simulationDuration + " ms")
    )

  override def notifyStat(averageSpeed: Double): Unit =
    SwingUtilities.invokeLater(() => updateView("[STAT]: average speed: " + averageSpeed))

  override def simulationStopped(): Unit = {}

  private def setFrameProperties(): Unit =
    setLayout(new GridBagLayout)
    setTitle("Car simulator")
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    setSize(defaultSize, defaultSize)
    setLocationRelativeTo(null)
    setResizable(false)
    setVisible(true)

  private def editAllComponentsProperties(): Unit =
    areaConsoleLog.setMargin(Insets(10, 10, 10, 10))
    areaConsoleLog.setEditable(false)
    populateComboBox()
    buttonStop.setEnabled(false)
    labelNumberOfSteps.setFont(bigFont)
    labelConsoleLog.setFont(bigFont)
    labelBox.setFont(bigFont)
    fieldNumberOfSteps.setFont(smallFont)
    checkBox.setFont(smallFont)
    buttonStart.setFont(smallFont)
    buttonStop.setFont(smallFont)
    buttonReset.setFont(smallFont)
    areaConsoleLog.setFont(smallFont)

  private def addAllComponentsIntoFrame(): Unit =
    val constraints = new GridBagConstraints
    constraints.gridx = 0
    constraints.gridy = 0
    constraints.insets = Insets(30, 16, 0, 16)
    constraints.anchor = GridBagConstraints.LINE_START
    constraints.fill = GridBagConstraints.HORIZONTAL
    add(inputContainer, constraints)
    constraints.gridx = 0
    constraints.gridy = 5
    constraints.insets = Insets(16, 16, 0, 16)
    constraints.fill = GridBagConstraints.NONE
    constraints.fill = GridBagConstraints.NONE
    add(labelBox, constraints)
    constraints.gridx = 0
    constraints.gridy = 6
    constraints.ipadx = 500
    constraints.fill = GridBagConstraints.NONE
    add(comboBox, constraints)
    constraints.gridx = 0
    constraints.gridy = 7
    constraints.ipadx = 0
    constraints.fill = GridBagConstraints.NONE
    add(checkBox, constraints)
    constraints.gridx = 0
    constraints.gridy = 8
    constraints.ipadx = 0
    constraints.fill = GridBagConstraints.NONE
    add(labelConsoleLog, constraints)
    constraints.gridx = 0
    constraints.gridy = 9
    constraints.ipady = 200
    constraints.ipadx = 500
    constraints.fill = GridBagConstraints.HORIZONTAL
    add(scroll, constraints)
    constraints.gridx = 0
    constraints.gridy = 10
    constraints.ipady = 10
    constraints.ipadx = 10
    constraints.fill = GridBagConstraints.HORIZONTAL
    add(buttonContainer, constraints)

  private def setViewComponents(): Unit = 
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
    inputContainer.add(labelNumberOfSteps)
    inputContainer.add(fieldNumberOfSteps)
    buttonContainer.add(buttonStart)
    buttonContainer.add(buttonStop)
    buttonContainer.add(buttonReset)

  private def updateView(message: String): Unit =
    areaConsoleLog.append(message + "\n")
    areaConsoleLog.setCaretPosition(areaConsoleLog.getDocument.getLength)

  private def numberOfSteps: Option[Int] =
    fieldNumberOfSteps.getText.toIntOption

  private def validateInput: Boolean =
    if numberOfSteps.isEmpty then
      displayMessageDialog("Number of steps isn't an integer"); false
    else true

  private def displayMessageDialog(message: String): Unit =
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)

  private def clearTextArea(): Unit =
    areaConsoleLog.setText("")

  private def populateComboBox(): Unit =
    for st <- SimulationType.values do comboBox.addItem(st)

  private def simulationType: SimulationType =
    comboBox.getSelectedItem.asInstanceOf[SimulationType]

  private def showView: Boolean =
    checkBox.isSelected

  private def updateViewWhenSimulationStart(): Unit =
    buttonStart.setEnabled(false)
    buttonStop.setEnabled(true)
    buttonReset.setEnabled(false)
    buttonStart.setText("Restart Simulation")
    simulationStarted = true

  private def resetView(): Unit =
    buttonStart.setEnabled(true)
    buttonStop.setEnabled(false)
    buttonReset.setEnabled(true)
    buttonStart.setText("Start simulation")
    simulationStarted = false


