package pt2.view;

import javax.swing.*;
import java.awt.*;

public class SolveMenu extends JFrame {

    private final JPanel panel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane(this.panel);

    public SolveMenu() {
        this.buildFrame();
        this.buildComponents();
        this.addComponentsInFrame();
        this.attachListener();
        this.attachMouseAdapter();
        this.spawnFrameAtCenter();
    }

    private void spawnFrameAtCenter(){
        this.setLocation(Utils.computeCenteredXDimension(this.getWidth()), Utils.computeCenteredYDimension(this.getHeight()));
    }

    private void attachMouseAdapter() {
    }

    private void attachListener() {
    }

    private void addComponentsInFrame() {
        this.add(this.scrollPane);
    }

    private void buildComponents() {
        this.panel.setLayout(new GridLayout(0, 5, 10, 10));

        //Create button example
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton("Sudoku " + i);
            button.setPreferredSize(new Dimension(80, 80));
            this.panel.add(button);
        }

        //Scroll interface
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    private void buildFrame() {
        this.setTitle("Sudoku Solver");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setResizable(false);
    }

    public void display(){
        this.setVisible(true);
    }

}
