package org.src.view;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.User;
import org.src.controller.GridController;
import org.src.model.GridImpl;
import org.src.model.LogicsImpl;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class SudokuGridView extends JFrame implements GridController {

    private static final int GRID_SIZE = 9;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 600;
    private JTextField[][] cells;
    private JPanel topPanel;
    private JLabel labelUsername;
    private JLabel gridIdLabel;
    private JButton backButton;
    private Grid grid;
    private String gridId;
    private final ScreenManager screenManager;
    private JPanel gridPanel;
    private LogicsImpl logics;
    private User user;

    public SudokuGridView(ScreenManager screenManager) throws IOException, TimeoutException {
        this.screenManager = screenManager;
        this.grid = new GridImpl();
        this.buildFrame();
        this.spawnFrameAtCenter();
        this.buildComponents();
        this.attachListener();
        this.addComponentsInFrame();
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
        this.labelUsername = new JLabel("Username: " + Utils.getUsername());
        this.gridIdLabel = new JLabel("GridId: " + gridId);
        this.backButton = new JButton("<-- Back");
        this.grid = new GridImpl();
        this.topPanel = new JPanel(new FlowLayout());
        this.gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        this.topPanel.add(labelUsername);
        this.topPanel.add(gridIdLabel);
    }

    private void attachListener(){
        this.backButton.addActionListener(e -> {
            this.screenManager.switchScreen("menu");
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
        Cell cellInGrid = this.grid.getCells().get(row * GRID_SIZE + col);

        //Set colors of cells
        if(cellInGrid.isImmutable()){
            cell.setBackground(Color.LIGHT_GRAY);
        }else{
            cell.setBackground(this.determinateColor(row, col));
        }

        //Set numbers in cells
        if(cellInGrid.getNumber().isPresent()){
            cell.setText(String.valueOf(cellInGrid.getNumber().get()));
        }

        this.addListenersToCell(cell, row, col);
        this.configureCellApparence(cell, row, col);
    }

    //Evaluate color of the cell
    private Color determinateColor(int row, int col){
        return this.grid.getCells().get(row * GRID_SIZE + col).isSelected()
                .map(User::getColor)
                .orElse(Color.WHITE);
    }

    //Add Listeners for the cells in grid
    private void addListenersToCell(JTextField cell, int row, int col){
        cell.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                try {
                    logics.selectCell(grid, user, row, col);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        cell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '1' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                } else {
                    try {
                        logics.makeMove(grid, user, Character.getNumericValue(c));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    private void configureCellApparence(JTextField cell, int row, int col){
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
    public void onGridReady(LogicsImpl logics, User user, String gridId, Grid grid) {
        this.logics = logics;
        this.user = user;
        this.gridId = gridId;
        this.grid = grid;
        this.gridIdLabel.setText("GridId: " + this.gridId);
        this.populateGrid();
        this.setVisible(true);
    }

    @Override
    public void updateGrid(Grid grid) {
        this.grid = grid;

        IntStream.range(0, GRID_SIZE).forEach(row ->
                IntStream.range(0, GRID_SIZE).forEach(col -> {
                    JTextField cell = this.cells[row][col];
                    Cell cellInGrid = this.grid.getCells().get(row * GRID_SIZE + col);

                    // Update cell color
                    if (cellInGrid.isImmutable()) {
                        cell.setBackground(Color.LIGHT_GRAY);
                    } else {
                        cell.setBackground(this.determinateColor(row, col));
                    }

                    // Update number of cell if is present
                    if (cellInGrid.getNumber().isPresent()) {
                        cell.setText(String.valueOf(cellInGrid.getNumber().get()));
                    } else {
                        cell.setText("");
                    }
                })
        );

        // Update UI
        this.gridPanel.revalidate();
        this.gridPanel.repaint();
    }
}

