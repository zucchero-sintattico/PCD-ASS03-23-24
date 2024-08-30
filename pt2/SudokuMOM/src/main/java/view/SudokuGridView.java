package view;

import logic.grid.cell.Cell;
import logic.grid.Grid;
import logic.user.User;
import logic.Controller;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class SudokuGridView extends JFrame implements SudokuView {

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
    private boolean won;

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
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    controller.leave();
                } catch (IOException | TimeoutException | NullPointerException ignored) {}
            }
        });
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
            this.screenManager.switchScreen(Screen.MENU);
            this.won = false;
            IntStream.range(0, 9).forEach(row ->
                    IntStream.range(0, 9).forEach(col -> {
                        cells[row][col].setBackground(Color.WHITE);
                        cells[row][col].setText("");
                    }));
            try {
                this.controller.leave();
            } catch (IOException | TimeoutException | NullPointerException ignored) {}
            this.dispose();
        });
    }

    private void addComponentsInFrame(){
        this.add(this.topPanel, BorderLayout.NORTH);
        this.add(this.backButton, BorderLayout.SOUTH);
        this.add(this.gridPanel, BorderLayout.CENTER);
    }

    private void populateGrid(){
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
        cell.setForeground(Color.BLACK);
        cell.setEnabled(false);
        cell.setDisabledTextColor(Color.BLACK);
        cell.setSelectedTextColor(Color.BLACK);
        cell.setBackground(Color.WHITE);
        this.addListenersToCell(cell, row, col);
        this.configureCellAppearance(cell, row, col);
    }

    private Color determinateColor(Grid grid, int row, int col){
        return grid.getCells().get(row * GRID_SIZE + col).isSelected()
                .map(User::color)
                .orElse(Color.WHITE);
    }

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
            public void focusLost(FocusEvent e) {}
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
                    } catch (IllegalArgumentException ex){
                        e.consume();
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
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.gridIdLabel.setText("GridID: " + this.controller.getGridId());
        this.labelUsername.setText("Username: " + this.controller.getUser().name());
        this.requestFocus();
    }

    @Override
    public void update(Grid grid) {
        SwingUtilities.invokeLater(()-> {
            IntStream.range(0, GRID_SIZE).forEach(row ->
                    IntStream.range(0, GRID_SIZE).forEach(col -> {
                        JTextField cell = this.cells[row][col];
                        Cell cellInGrid = grid.getCells().get(row * GRID_SIZE + col);

                        if (cellInGrid.isImmutable()) {
                            cell.setBackground(Color.LIGHT_GRAY);
                            cell.setEnabled(false);
                        } else {
                            cell.setBackground(Color.WHITE);
                            cell.setEnabled(true);
                            cell.setBackground(this.determinateColor(grid, row, col));
                        }
                        
                        if (cellInGrid.getNumber().isPresent()) {
                            cell.setText(String.valueOf(cellInGrid.getNumber().get()));
                        } else {
                            cell.setText("");
                        }
                    })
            );

            if (grid.haveWon()) {
                this.disableAllCells();
                if(!this.won){
                    this.won = true;
                    MessageDialog.showMessage(this, "Victory", "You have won");
                }
            }
        });
    }

    private void disableAllCells(){
        IntStream.range(0, 9).forEach(row ->
                IntStream.range(0, 9).forEach(col -> {
                    this.cells[row][col].setEnabled(false);
                }));
    }
    
}