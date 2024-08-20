package org.src.view;

import org.src.common.Grid;
import org.src.common.User;
import org.src.model.LogicsImpl;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.Border;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class GridView extends JFrame {
    private final JTextField[][] cells;
    private final LogicsImpl logics;
    private final JLabel usernameLabel;
    private final User user;
    private final Grid grid;

    public GridView(LogicsImpl logic, User user, Grid grid) throws IOException {
        this.logics = logic;
        this.user = user;
        this.cells = new JTextField[9][9];
        this.usernameLabel = new JLabel("Username: " + user.getName());
        this.grid = grid;
        this.build();
    }

    public void build() throws IOException {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Top panel for username
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        topPanel.add(this.usernameLabel);

        this.add(topPanel, BorderLayout.NORTH);

        // Center panel for Sudoku grid
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(9, 9));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                this.cells[row][col] = new JTextField();
                int number = this.grid.getCells().get(row * 9 + col).getNumber().orElse(0);
                Color background = Color.WHITE;
                if (this.grid.getCells().get(row * 9 + col).isSelected().isPresent()) {
                    background = this.grid.getCells().get(row * 9 + col).isSelected().get().getColor();
                }
                this.cells[row][col].setBackground(background);
                if (number != 0) {
                    this.cells[row][col].setText(String.valueOf(number));
                } else {
                    int finalRow = row;
                    int finalCol = col;
                    this.cells[row][col].addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            try {
                                logics.selectCell(grid, user, finalRow, finalCol);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                        }

                    });
                    this.cells[row][col].addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            char c = e.getKeyChar();
                            if (!((c >= '1' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                                e.consume();
                            }
                            try {
                                logics.makeMove(grid, user, Character.getNumericValue(c));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                        }
                    });
                }
                this.cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                this.cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));

                // Set borders for 3x3 subgrid highlighting
                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == 8) ? 2 : 1;
                int right = (col == 8) ? 2 : 1;
                Border border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
                this.cells[row][col].setBorder(border);

                gridPanel.add(this.cells[row][col]);
            }
        }

        this.add(gridPanel, BorderLayout.CENTER);
        this.setSize(600, 600);
        this.setVisible(true);
    }

    public void updateGridView() throws IOException {
         for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                 int number = this.grid.getCells().get(row * 9 + col).getNumber().orElse(0);
                    Color background = Color.WHITE;
                    if (this.grid.getCells().get(row * 9 + col).isSelected().isPresent()) {
                        background = this.grid.getCells().get(row * 9 + col).isSelected().get().getColor();
                    }
                    this.cells[row][col].setBackground(background);
                 if (number != 0) {
                      this.cells[row][col].setText(String.valueOf(number));
                 } else {
                      this.cells[row][col].setText("");
                 }
                }
          }
    }


}