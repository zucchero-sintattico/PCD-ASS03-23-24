package pt2.view;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {

    private final JButton newGame = new JButton("New Game");
    private final JButton resolve = new JButton("Resolve Sudoku");
    private final JPanel panel = new JPanel();
    private final JLabel title = new JLabel("Sudoku Game", SwingConstants.CENTER);

    public Menu() {
        this.buildFrame();
        this.buildComponents();
        this.addComponentsInFrame();
    }

    private void buildFrame(){
        this.setTitle("Menu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 400);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
    }

    private void buildComponents(){
        Font arial = new Font("Arial", Font.PLAIN, 16);
        Font serif = new Font("Serif", Font.BOLD, 24);
        this.newGame.setPreferredSize(new Dimension(300, 50));
        this.newGame.setFont(arial);
        this.newGame.setFocusPainted(false);
        this.resolve.setPreferredSize(new Dimension(300, 50));
        this.resolve.setFont(arial);
        this.resolve.setFocusPainted(false);
        this.title.setFont(serif);
        this.title.setPreferredSize(new Dimension(500, 100));
        this.panel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.panel.add(this.newGame, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        this.panel.add(this.resolve, gridBagConstraints);
    }

    private void addComponentsInFrame(){
        this.add(this.title, BorderLayout.NORTH);
        this.add(this.panel);
    }

    public void display(){
        this.setVisible(true);
    }


    public static void main(String[] args) {
        Menu menu = new Menu();
        SwingUtilities.invokeLater(menu::display);
    }
}
