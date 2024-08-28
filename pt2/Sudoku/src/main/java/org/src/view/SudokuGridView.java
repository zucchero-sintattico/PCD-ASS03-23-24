package org.src.view;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.User;
import org.src.controller.Controller;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.stream.IntStream;

public class SudokuGridView extends JFrame implements SudokuView{

    private static final int GRID_SIZE = 9;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 600;
    private JTextField[][] cells;
    private JPanel topPanel;
    private JLabel labelUsername = new JLabel();
    private JLabel gridIdLabel = new JLabel();
    private JButton backButton;
    private final ScreenManager screenManager;
    private JPanel gridPanel;
    private final Controller controller;

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.requestFocus();
    }

    public SudokuGridView(ScreenManager screenManager, Controller controller) {
        this.screenManager = screenManager;
        this.controller = controller;
        this.buildFrame();
        this.spawnFrameAtCenter();
        this.buildComponents();
        this.attachListener();
        this.addComponentsInFrame();
        this.populateGrid();
    }

    private void buildFrame(){
        this.setTitle("Sudoku Grid");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
    }

    private void spawnFrameAtCenter(){
        this.setLocationRelativeTo(null);
    }

    private void buildComponents(){
        this.cells = new JTextField[9][9];
        this.labelUsername = new JLabel("Username: ");
        this.gridIdLabel = new JLabel("GridID: ");
        this.backButton = new JButton("<-- Back");
        this.topPanel = new JPanel(new FlowLayout());
        this.gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        this.topPanel.add(labelUsername);
        this.topPanel.add(gridIdLabel);
    }

    private void attachListener(){
        this.backButton.addActionListener(e -> {
            this.screenManager.switchScreen("menu");
            IntStream.range(0, 9).forEach(row ->
                    IntStream.range(0, 9).forEach(col -> {
                        cells[row][col].setBackground(Color.WHITE);
                        cells[row][col].setText("");
                    }));
            controller.leave();
            this.dispose();
        });
    }

    private void addComponentsInFrame(){
        this.add(this.topPanel, BorderLayout.NORTH);
        this.add(this.backButton, BorderLayout.SOUTH);
        this.add(this.gridPanel, BorderLayout.CENTER);
    }

    public void populateGrid(){
        IntStream.range(0, 9).forEach(row ->
                IntStream.range(0, 9).forEach(col -> {
                    this.createAndConfigureCell(row, col);
                    this.gridPanel.add(this.cells[row][col]);
                }));
    }

    private void createAndConfigureCell(int row, int col){
        JTextField cell = new JTextField();
        this.cells[row][col] = cell;
        cell.setBackground(Color.WHITE);
        this.addListenersToCell(cell, row, col);
        this.configureCellAppearance(cell, row, col);
    }

    //Evaluate color of the cell
    private Color determinateColor(Grid grid, int row, int col){
        return grid.getCells().get(row * GRID_SIZE + col).isSelected()
                .map(User::getColor)
                .orElse(Color.WHITE);
    }

    //Add Listeners for the cells in grid
    private void addListenersToCell(JTextField cell, int row, int col){
        cell.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                try {
                    controller.selectCell(row, col);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //nothing to do
            }
        });

        cell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c >= '1' && c <= '9')) {
                    try {
                        controller.makeMove(Character.getNumericValue(c));
                        requestFocus();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    e.consume();
                }
            }
        });
    }

    private void configureCellAppearance(JTextField cell, int row, int col){
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("Arial", Font.BOLD, 20));

        int top = (row % 3 == 0) ? 2 : 1;
        int left = (col % 3 == 0) ? 2 : 1;
        int bottom = (row == 8) ? 2 : 1;
        int right = (col == 8) ? 2 : 1;

        Border border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
        cell.setBorder(border);
    }

    @Override
    public void update(Grid grid) {
        SwingUtilities.invokeLater(()-> {
            this.gridIdLabel.setText("GridID: " + this.controller.getGridId());
            this.labelUsername.setText("Username: " + this.controller.getUser().getName());
            IntStream.range(0, GRID_SIZE).forEach(row ->
                    IntStream.range(0, GRID_SIZE).forEach(col -> {
                        JTextField cell = this.cells[row][col];
                        Cell cellInGrid = grid.getCells().get(row * GRID_SIZE + col);

                        // Update cell color
                        if (cellInGrid.isImmutable()) {
                            cell.setBackground(Color.LIGHT_GRAY);
                            cell.setFocusable(false);
                        } else {
                            cell.setBackground(this.determinateColor(grid, row, col));
                        }

                        // Update number of cell if is present
                        if (cellInGrid.getNumber().isPresent()) {
                            cell.setText(String.valueOf(cellInGrid.getNumber().get()));
                        } else {
                            cell.setText("");
                        }
                    })
            );

            if (grid.haveWon()) {
                MessageDialog.showMessage(this, "Victory", "You have won");
                this.disableAllCells();
            }
        });
    }

    private void disableAllCells(){
        IntStream.range(0, 9).forEach(row ->
                IntStream.range(0, 9).forEach(col -> {
                    this.cells[row][col].setBackground(Color.LIGHT_GRAY);
                    this.cells[row][col].setFocusable(false);
                }));
    }

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

}