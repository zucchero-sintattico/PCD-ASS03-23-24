package pt2.view;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

class Grid extends JFrame{
    
    //Properties
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private JPanel container;
    private Logics logics;

    //Constructor
    public Grid(){
        this.frameProperties();
        this.build();
    }

    //Method
    private void frameProperties(){
        this.setTitle("Sudoku Grid");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void build(){
        this.container = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        this.logics = new LogicsImpl();

        //Build grid
        IntStream.range(0, GRID_SIZE).forEach(row -> {
            IntStream.range(0, GRID_SIZE).forEach(column -> {
                int number = this.logics.getNumber(row, column);
                JTextField textField = new JTextField();
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.BOLD, 20));
                textField.setText(String.valueOf(number));
                textField.setEditable(number == 0);
                this.container.add(textField);

                //Add border to sub grid
                int top = (row % SUBGRID_SIZE == 0) ? 2 : 1;
                int left = (column % SUBGRID_SIZE == 0) ? 2 : 1;
                int bottom = (row == GRID_SIZE - 1) ? 2 : 1;
                int right = (column == GRID_SIZE - 1) ? 2 : 1;
                textField.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
            });
        });

        this.add(container);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new Grid().setVisible(true);});
    }
}