package pt2.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SolveMenu extends JFrame {

    private final JPanel panel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane(this.panel);
    private final JButton back = new JButton("< - Back");

    public SolveMenu() {
        this.buildFrame();
        this.buildComponents();
        this.addComponentsInFrame();
        this.attachListener();
        this.spawnFrameAtCenter();
    }

    private void attachListener() {
        this.back.addActionListener(e -> {
            this.dispose();
            Menu menu = new Menu();
            SwingUtilities.invokeLater(menu::display);
        });
    }

    private void spawnFrameAtCenter(){
        this.setLocation(Utils.computeCenteredXDimension(this.getWidth()), Utils.computeCenteredYDimension(this.getHeight()));
    }

    private void addComponentsInFrame() {
        this.add(this.scrollPane);
        this.add(this.back, BorderLayout.SOUTH);
    }

    private void buildComponents() {
        this.panel.setLayout(new GridLayout(0, 5, 10, 10));

        //Create button example
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton("Sudoku " + i);
            button.setPreferredSize(new Dimension(80, 80));
            button.setFont(new Font("Arial", Font.PLAIN, 16));
            this.panel.add(button);

            //Mouse Enter and Mouse Leave control
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    //Do........
                    button.setFont(new Font("Arial", Font.BOLD, 16));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setFont(new Font("Arial", Font.PLAIN, 16));
                }
            });
        }

        //Scroll interface
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    private void buildFrame() {
        this.setTitle("Sudoku Solver");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 500);
        this.setResizable(false);
    }

    public void display(){
        this.setVisible(true);
    }

}
