package pt2.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Menu extends JFrame {

    private final JButton newGame = new JButton("New Game");
    private final JButton resolve = new JButton("Resolve Sudoku");
    private final JButton changeNickName = new JButton("Change Nickname");
    private final JPanel panel = new JPanel();
    private final JLabel title = new JLabel("Sudoku Game", SwingConstants.CENTER);

    public Menu() {
        this.buildFrame();
        this.buildComponents();
        this.addComponentsInFrame();
        this.attachListener();
        this.attachMouseAdapter();
        this.spawnFrameAtCenter();
    }

    private void buildFrame(){
        this.setTitle("Menu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 400);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
    }

    private void spawnFrameAtCenter(){
        this.setLocation(Utils.computeCenteredXDimension(this.getWidth()), Utils.computeCenteredYDimension(this.getHeight()));
    }

    private void buildComponents(){
        Font arial = new Font("Arial", Font.PLAIN, 16);
        Font serif = new Font("Serif", Font.BOLD, 24);
        Dimension buttonSize = new Dimension(300, 50);
        this.newGame.setPreferredSize(buttonSize);
        this.newGame.setFont(arial);
        this.newGame.setFocusPainted(false);
        this.changeNickName.setPreferredSize(buttonSize);
        this.changeNickName.setFont(arial);
        this.changeNickName.setFocusPainted(false);
        this.resolve.setPreferredSize(buttonSize);
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
        gridBagConstraints.gridy = 2;
        this.panel.add(this.changeNickName, gridBagConstraints);
    }

    private void attachListener(){
        this.changeNickName.addActionListener(e -> {
            this.dispose();
            Login login = new Login();
            SwingUtilities.invokeLater(login::display);
        });

        this.newGame.addActionListener(e -> {
            //Do something
            this.dispose();
            GUI gui = new GUI(40, Utils.getUsername());
            SwingUtilities.invokeLater(gui::display);
        });

        this.resolve.addActionListener(e -> {
            this.dispose();
            SolveMenu solveMenu = new SolveMenu();
            SwingUtilities.invokeLater(solveMenu::display);
        });
    }

    private void attachMouseAdapter(){
        this.newGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                newGame.setText("< New Game >");
                newGame.setFont(new Font("Arial", Font.BOLD, 16));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                newGame.setText("New Game");
                newGame.setFont(new Font("Arial", Font.PLAIN, 16));
            }
        });

        this.resolve.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resolve.setText("< Resolve Sudoku >");
                resolve.setFont(new Font("Arial", Font.BOLD, 16));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                resolve.setText("Resolve Sudoku");
                resolve.setFont(new Font("Arial", Font.PLAIN, 16));
            }
        });

        this.changeNickName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                changeNickName.setText("< Change Nickname >");
                changeNickName.setFont(new Font("Arial", Font.BOLD, 16));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                changeNickName.setText("Change Nickname");
                changeNickName.setFont(new Font("Arial", Font.PLAIN, 16));
            }
        });
    }

    private void addComponentsInFrame(){
        this.add(this.title, BorderLayout.NORTH);
        this.add(this.panel);
    }

    public void display(){
        this.setVisible(true);
    }

}
