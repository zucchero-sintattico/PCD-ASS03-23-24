package view

import javax.swing.WindowConstants
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.GridBagConstraints
import java.awt.FlowLayout
import java.awt.Font
import java.util
import java.util.Optional
import java.util.function.Consumer
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
import utils.Vector2D
import logic.SimulationType


object StatisticalView {
  private val DEFAULT_SIZE = 1000
}

class StatisticalView extends JFrame with SimulationListener with Clickable  {
  setFrameProperties()
  // Set controller
  //        this.controller = new Controller();
  // Create components
  setViewComponents()
  // Add components on panel
  addAllComponentsIntoFrame()
  // Add properties
  editAllComponentsProperties()
  this.pack()

  // Create frame
  private var labelNumberOfSteps: JLabel = new JLabel("Number of steps")
  private var fieldNumberOfSteps: JTextField = null
  private var labelNumberOfThreads: JLabel = null
  private var fieldNumberOfThreads: JTextField = null
  private var labelConsoleLog: JLabel = null
  private var areaConsoleLog: JTextArea = null
  private var buttonStart: JButton = null
  private var buttonReset: JButton = null
  private var buttonStop: JButton = null
  private var labelBox: JLabel = null
  private var comboBox: JComboBox[SimulationType] = null
  private var checkBox: JCheckBox = null
  private var checkBoxAvaiableProcessor: JCheckBox = null
  private var scroll: JScrollPane = null
  private var inputContainer: JPanel = null
  private var buttonContainer: JPanel = null
  //    private Controller controller;
  private var isStartedSimulation = false

  private def setFrameProperties(): Unit = {
    this.setLayout(new GridBagLayout)
    this.setTitle("Car simulator")
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
//    this.setDefaultCloseOperation(WindowConstant.EXIT_ON_CLOSE)
    this.setSize(StatisticalView.DEFAULT_SIZE, StatisticalView.DEFAULT_SIZE)
    this.setLocationRelativeTo(null)
    this.setResizable(false)
    display()
  }

  private def editAllComponentsProperties(): Unit = {
    this.areaConsoleLog.setMargin(new Insets(10, 10, 10, 10))
    this.areaConsoleLog.setEditable(false)
    this.populateComboBox()
    this.buttonStop.setEnabled(false)
    // this.buttonStart.addActionListener(this);
    // this.buttonStop.addActionListener(this);
    // this.buttonReset.addActionListener(this);
    // this.checkBoxAvaiableProcessor.addActionListener(this);
    this.checkBoxAvaiableProcessor.setSelected(true)
    this.labelNumberOfSteps.setFont(new Font(getName, Font.PLAIN, 16))
    this.labelNumberOfThreads.setFont(new Font(getName, Font.PLAIN, 16))
    this.labelConsoleLog.setFont(new Font(getName, Font.PLAIN, 16))
    this.labelBox.setFont(new Font(getName, Font.PLAIN, 16))
    this.fieldNumberOfSteps.setFont(new Font(getName, Font.PLAIN, 14))
    this.fieldNumberOfThreads.setFont(new Font(getName, Font.PLAIN, 14))
    this.checkBox.setFont(new Font(getName, Font.PLAIN, 14))
    this.checkBoxAvaiableProcessor.setFont(new Font(getName, Font.PLAIN, 14))
    this.buttonStart.setFont(new Font(getName, Font.PLAIN, 14))
    this.buttonStop.setFont(new Font(getName, Font.PLAIN, 14))
    this.buttonReset.setFont(new Font(getName, Font.PLAIN, 14))
    this.areaConsoleLog.setFont(new Font(getName, Font.PLAIN, 14))
  }

  private def addAllComponentsIntoFrame(): Unit = {
    val constraints = new GridBagConstraints
    constraints.gridx = 0
    constraints.gridy = 0
    constraints.insets = new Insets(30, 16, 0, 16)
    constraints.anchor = GridBagConstraints.LINE_START
    constraints.fill = GridBagConstraints.HORIZONTAL
    this.add(this.inputContainer, constraints)
    constraints.gridx = 0
    constraints.gridy = 4
    constraints.insets = new Insets(16, 16, 0, 16)
    constraints.fill = GridBagConstraints.NONE
    constraints.fill = GridBagConstraints.NONE
    this.add(this.checkBoxAvaiableProcessor, constraints)
    constraints.gridx = 0
    constraints.gridy = 5
    constraints.insets = new Insets(16, 16, 0, 16)
    constraints.fill = GridBagConstraints.NONE
    constraints.fill = GridBagConstraints.NONE
    this.add(this.labelBox, constraints)
    constraints.gridx = 0
    constraints.gridy = 6
    constraints.ipadx = 500
    constraints.fill = GridBagConstraints.NONE
    this.add(this.comboBox, constraints)
    constraints.gridx = 0
    constraints.gridy = 7
    constraints.ipadx = 0
    constraints.fill = GridBagConstraints.NONE
    this.add(this.checkBox, constraints)
    constraints.gridx = 0
    constraints.gridy = 8
    constraints.ipadx = 0
    constraints.fill = GridBagConstraints.NONE
    this.add(this.labelConsoleLog, constraints)
    constraints.gridx = 0
    constraints.gridy = 9
    constraints.ipady = 200
    constraints.ipadx = 500
    constraints.fill = GridBagConstraints.HORIZONTAL
    this.add(this.scroll, constraints)
    constraints.gridx = 0
    constraints.gridy = 10
    constraints.ipady = 10
    constraints.ipadx = 10
    constraints.fill = GridBagConstraints.HORIZONTAL
    this.add(this.buttonContainer, constraints)
  }

  private def setViewComponents(): Unit = {
//    this.labelNumberOfSteps =
    this.fieldNumberOfSteps = new JTextField("200", 1)
    this.labelNumberOfThreads = new JLabel("Number of threads")
    this.fieldNumberOfThreads = new JTextField(this.getProcessor, 1)
    this.labelConsoleLog = new JLabel("Console log")
    this.areaConsoleLog = new JTextArea("Console log")
    this.buttonStart = new JButton("Start simulation")
    this.buttonReset = new JButton("Reset simulation")
    this.buttonStop = new JButton("Stop simulation")
    this.scroll = new JScrollPane(this.areaConsoleLog)
    this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
    this.labelBox = new JLabel("Choise simulation")
    this.comboBox = new JComboBox[SimulationType]
    this.checkBox = new JCheckBox("Display simulation view")
    this.fieldNumberOfThreads.setEnabled(false)
    this.checkBoxAvaiableProcessor = new JCheckBox("Use avaiable processor (max: " + getProcessor + ")")
    this.inputContainer = new JPanel(new GridLayout(2, 2, 10, 10))
    this.inputContainer.add(this.labelNumberOfSteps)
    this.inputContainer.add(this.labelNumberOfThreads)
    this.inputContainer.add(this.fieldNumberOfSteps)
    this.inputContainer.add(this.fieldNumberOfThreads)
    this.buttonContainer = new JPanel(new FlowLayout)
    this.buttonContainer.add(this.buttonStart)
    this.buttonContainer.add(this.buttonStop)
    this.buttonContainer.add(this.buttonReset)
  }

  private def getProcessor = {
    //return String.valueOf(this.controller.getAvailableProcessor());
    "not-used"
  }

  def display(): Unit = {
    SwingUtilities.invokeLater(() => this.setVisible(true))
  }

  def updateView(message: String): Unit = {
    this.areaConsoleLog.append(message + "\n")
    this.areaConsoleLog.setCaretPosition(this.areaConsoleLog.getDocument.getLength)
  }

  def getNumberOfSteps: Optional[Integer] = try Optional.of(Integer.valueOf(this.fieldNumberOfSteps.getText))
  catch {
    case e: Exception =>
      Optional.empty
  }

  def getNumberOfThreads: Optional[Integer] = try Optional.of(Integer.valueOf(this.fieldNumberOfThreads.getText))
  catch {
    case e: Exception =>
      Optional.empty
  }

  def validateInput: Boolean = if (this.getNumberOfSteps.isEmpty) {
    displayMessageDialog("Number of steps isn't an integer")
    false
  }
  else if (this.getNumberOfThreads.isEmpty) {
    displayMessageDialog("Number of threads isn't an integer")
    false
  }
  else true

  private def displayMessageDialog(message: String): Unit = {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
  }

  def clearTextArea(): Unit = {
    this.areaConsoleLog.setText("")
  }

  def populateComboBox(): Unit = {
    this.comboBox.addItem(SimulationType.SINGLE_ROAD_TWO_CAR)
    this.comboBox.addItem(SimulationType.SINGLE_ROAD_SEVERAL_CARS)
    this.comboBox.addItem(SimulationType.SINGLE_ROAD_WITH_TRAFFIC_TWO_CAR)
    this.comboBox.addItem(SimulationType.CROSS_ROADS)
    this.comboBox.addItem(SimulationType.MASSIVE_SIMULATION)
  }

  def getSimulationType: SimulationType = this.comboBox.getSelectedItem.asInstanceOf[SimulationType]

  def getShowViewFlag: Boolean = this.checkBox.isSelected

  override def notifyInit(t: Int, agents: List[Car]): Unit = {
    this.updateView("[Simulation]: START simulation")
  }

  override def notifyStepDone(t: Int, roads: List[Road], agents: List[Car], trafficLights: List[TrafficLight]): Unit = {
    this.updateView("[STAT] Steps: " + t)
  }

  override def whenClicked(clickMessage: ViewClickRelayActor.Command => Unit): Unit = {

    //         this.buttonStart.addActionListener(e -> {
    //             if (!this.isStartedSimulation) {
    //                 SwingUtilities.invokeLater(() -> {
    //                     if(validateInput()){
    //                         updateViewWhenSimulationStart();
    //                         this.clearTextArea();
    //                         clickMessage.accept(ViewClickRelayActor.Command.SetupSimulation(this.getSimulationType(), this.getNumberOfSteps().get(), this.getShowViewFlag()))
    //                         clickMessage.accept(ViewClickRelayActor.Command.StartSimulation);
    // //                        this.controller.setupSimulation(this.getSimulationType(), this.getNumberOfSteps().get(), this.getNumberOfThreads().get());
    // //                        if(this.getShowViewFlag()){
    // //                            this.controller.showView();
    // //                        }
    // //                        this.controller.attachListener(this);
    // //                        this.controller.startSimulation();
    //                     }
    //                 });
    //             } else {
    //                 SwingUtilities.invokeLater(() -> {
    //                     updateViewWhenSimulationStart();
    //                     clickMessage.accept(ViewClickRelayActor.Command.StartSimulation);
    // //                    this.controller.startSimulation();
    //                 });
    //             }
    //         });
    //         this.buttonReset.addActionListener(e -> {
    //             SwingUtilities.invokeLater(() -> {
    //                 if(validateInput()){
    //                     this.clearTextArea();
    //                     this.areaConsoleLog.setText("Console log");
    //                     this.resetView();
    //                     clickMessage.accept(ViewClickRelayActor.Command.SetupSimulation(this.getSimulationType(), this.getNumberOfSteps().get(), this.getShowViewFlag()))
    // //                    this.controller.setupSimulation(this.getSimulationType(), this.getNumberOfSteps().get(), this.getNumberOfThreads().get());
    //                 }
    //             });
    //         }
    //         this.buttonStop.addActionListener(e -> {
    //             SwingUtilities.invokeLater(() -> {
    //                 this.buttonStart.setEnabled(true);
    //                 this.buttonStop.setEnabled(false);
    //                 this.buttonReset.setEnabled(true);
    //                 clickMessage.accept(ViewClickRelayActor.Command.StopSimulation);
    // //                this.controller.stopSimulation();
    //             });
    //         });
  }

  private def updateViewWhenSimulationStart(): Unit = {
    this.buttonStart.setEnabled(false)
    this.buttonStop.setEnabled(true)
    this.buttonReset.setEnabled(false)
    this.buttonStart.setText("Restart Simulation")
    this.isStartedSimulation = true
  }

  override def notifySimulationEnded(simulationDuration: Int): Unit = {
    resetView()
    this.updateView("[SIMULATION] Time: " + simulationDuration + " ms")
  }

  private def resetView(): Unit = {
    this.buttonStart.setEnabled(true)
    this.buttonStop.setEnabled(false)
    this.buttonReset.setEnabled(true)
    this.buttonStart.setText("Start simulation")
    this.isStartedSimulation = false
  }

  override def notifyStat(averageSpeed: Double): Unit = {
    this.updateView("[STAT]: average speed: " + averageSpeed)
  }
}

