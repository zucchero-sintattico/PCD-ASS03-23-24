package pt2.view;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.Border;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class GUI extends JFrame {
    private final JTextField[][] cells;
    private final LogicsImpl logics;
    private final JLabel usernameLabel;

    public GUI(int emptyCells, String username) {
        this.logics = new LogicsImpl(emptyCells);
        this.cells = new JTextField[9][9];
        this.usernameLabel = new JLabel("Username: " + username);
        this.build();
    }

    public void build() {
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
                int number = this.logics.getNumber(row, col);
                if (number != 0) {
                    this.cells[row][col].setText(String.valueOf(number));
                    this.cells[row][col].setEditable(false);
                } else {
                    this.cells[row][col].setEditable(true);
                    this.cells[row][col].addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            // Do nothing when focus is gained
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            checkCell((JTextField) e.getSource());
                            if(logics.won()){
                                JOptionPane.showMessageDialog(null, "You have won", "Sudoku Game", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                        private void checkCell(JTextField textField) {
                            String text = textField.getText();
                            if (text.matches("\\d")) {
                                int number = Integer.parseInt(text);
                                for (int i = 0; i < 9; i++) {
                                    for (int j = 0; j < 9; j++) {
                                        if (cells[i][j] == textField) {
                                            if (logics.hit(i, j, number)) {
                                                textField.setBackground(Color.GREEN);
                                            } else {
                                                textField.setBackground(Color.RED);
                                            }
                                            return;
                                        }
                                    }
                                }
                            } else {
                                textField.setBackground(Color.WHITE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI(40, "Alecs00"));
    }
}
