package org.src.view;

import org.src.model.Controller;
import org.src.common.Point2d;
import org.src.model.grid.cell.Cell;
import org.src.model.grid.SudokuGrid;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.Border;
import java.awt.event.*;
import java.rmi.RemoteException;

public class GridView extends JFrame implements Changeable {

    private final Font numberFont = new Font("Arial", Font.BOLD, 20);
    private final JTextField[][] cells;
    private final JLabel usernameLabel = new JLabel();
    private final JButton back = new JButton("< - Back");

    private final Controller controller;
    private Runnable changeScreen = () -> {};
    private boolean initialized;

    public GridView(Controller controller) {
        this.controller = controller;
        this.cells = new JTextField[9][9];
        this.build();
        this.attachListener();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.moveFocusToFrame();
    }

    private void moveFocusToFrame(){
        this.requestFocus();
    }

    public void build(){
        this.setSize(600, 600);
        this.setLayout(new BorderLayout());
        this.add(this.buildTopPanel(), BorderLayout.NORTH);
        this.add(this.buildGrid(), BorderLayout.CENTER);
        this.add(this.back, BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(this.usernameLabel);
        return topPanel;
    }

    public JPanel buildGrid() {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(9, 9));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JTextField cellRender = new JTextField();
                this.cells[row][col] = cellRender;

                cellRender.setEnabled(false);
                cellRender.setHorizontalAlignment(JTextField.CENTER);
                cellRender.setFont(numberFont);
                cellRender.setDisabledTextColor(Color.BLACK);

                // Set borders for 3x3 subgrid highlighting
                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == 8) ? 2 : 1;
                int right = (col == 8) ? 2 : 1;
                Border border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
                cellRender.setBorder(border);

                Point2d position = new Point2d(row, col);
                this.addListenerToCell(cellRender, position);
                gridPanel.add(cellRender);
            }
        }
        return gridPanel;
    }

    private void addListenerToCell(JTextField cellRender, Point2d cellPosition) {
        this.addFocusListener(cellRender, cellPosition);
        this.addKeyListener(cellRender, cellPosition);
    }

    private void addKeyListener(JTextField cellRender, Point2d cellPosition) {
        cellRender.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                System.out.println(c == KeyEvent.VK_BACK_SPACE);
                System.out.println(c == KeyEvent.VK_DELETE);
                if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE){
                    if(cellRender.getText().isEmpty()){
                        try {
                            controller.removeCellNumber(cellPosition);
                            moveFocusToFrame();
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    }else{
                        e.consume();
                    }
                } else if (c >= '1' && c <= '9') {
                    try {
                        controller.updateCellNumber(cellPosition, Character.getNumericValue(c));
                        moveFocusToFrame();
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    } catch (IllegalArgumentException ex) {
                        e.consume();
                    }
                }else{
                    e.consume();
                }
            }
        });
    }

    private void addFocusListener(JTextField cellRender, Point2d cellPosition) {
        cellRender.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                try {
                    controller.selectCell(cellPosition);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //nothing to do
            }
        });
    }

    public void display(){
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    private void attachListener(){
        this.back.addActionListener(e -> {
            this.initialized = false;
            try {
                this.controller.leaveSudoku();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            this.changeScreen.run();
        });
    }

    @Override
    public void onChange(Runnable runnable) {
        this.changeScreen = runnable;
    }

    private void initCell(Cell cell) {
        JTextField cellRender = this.cells[cell.position().x()][cell.position().y()];
        cellRender.setEnabled(!cell.immutable());
    }

    public void update(SudokuGrid grid) {
        SwingUtilities.invokeLater(() -> {
            if(!this.initialized){
                this.usernameLabel.setText("Username: " + controller.getUsername());
                this.initialized = true;
                grid.cells().forEach(this::initCell);
            }
            grid.cells().forEach(this::updateCell);
        });
    }

    private void updateCell(Cell cell) {
        JTextField cellRender = this.cells[cell.position().x()][cell.position().y()];

        Color background = Color.WHITE;
        if(cell.immutable()){
            background = Color.LIGHT_GRAY;
        }else if(cell.user().isPresent()){
            background = cell.user().get().color();
        }

        String number = cell.number().map(String::valueOf).orElse("");

        cellRender.setBackground(background);
        cellRender.setText(number);
    }

}