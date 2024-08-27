package client.view;

import client.model.Controller;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

public class Menu extends JFrame implements Changeable{

    private final Font arial = new Font("Arial", Font.PLAIN, 16);
    private final Font arialBold = new Font("Arial", Font.BOLD, 16);

    private final JLabel usernameLabel = new JLabel("Username: ");
    private final JLabel sudokuIdLabel = new JLabel("Sudoku ID: ");
    private final JTextField usernameField = new JTextField(15);
    private final JTextField sudokuIdField = new JTextField(15);
    private final JButton create = new JButton("Create");
    private final JButton join = new JButton("Join");

    private final Controller controller;
    private Runnable changeScreen = () -> {};

    public Menu(Controller controller) {
        this.controller = controller;
        this.buildFrame("Menu");
        this.buildComponents();
        this.addComponentsInFrame();
        this.attachListener();
        this.setLocationRelativeTo(null);
    }

    @Override
    public void onChange(Runnable runnable) {
        this.changeScreen = runnable;
    }

    private void addComponentsInFrame() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        this.add(usernameLabel, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        this.add(usernameField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        this.add(sudokuIdLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        this.add(sudokuIdField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        this.add(create, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        this.add(join, gridBagConstraints);
    }

    private void buildComponents() {
        this.usernameLabel.setFont(arialBold);
        this.sudokuIdLabel.setFont(arialBold);
        this.usernameField.setFont(arial);
        this.sudokuIdField.setFont(arial);
        this.create.setFont(arial);
        this.join.setFont(arial);
        ((AbstractDocument) this.usernameField.getDocument()).setDocumentFilter(new LengthFilter(14));
        ((AbstractDocument) this.sudokuIdField.getDocument()).setDocumentFilter(new LengthFilter(14));
    }

    private void buildFrame(String title) {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(450, 200);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
    }

    private void showErrorDialog(String errorMessage){
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean validateInput(){
        if(Objects.equals(this.usernameField.getText(), "")){
            this.showErrorDialog("Invalid Username");
            return false;
        }
        if(Objects.equals(this.sudokuIdField.getText(), "")){
            this.showErrorDialog("Invalid Sudoku ID");
            return false;
        }
        return true;
    }

    private void attachListener(){
        this.create.addActionListener(e -> {
            if(this.validateInput()){
                try {
                    this.controller.createSudoku(this.usernameField.getText(), this.sudokuIdField.getText());
                } catch (RemoteException | NotBoundException ex) {
                    throw new RuntimeException(ex);
                }
                this.changeScreen.run();
            }
        });
        this.join.addActionListener(e -> {
            if(this.validateInput()){
                try {
                    this.controller.joinSudoku(this.usernameField.getText(), this.sudokuIdField.getText());
                } catch (RemoteException | NotBoundException ex) {
                    throw new RuntimeException(ex);
                }
                this.changeScreen.run();
            }
        });
    }

    /* To limit the len of the textField */
    private static class LengthFilter extends DocumentFilter {
        private final int maxLength;

        public LengthFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && (fb.getDocument().getLength() + string.length()) <= maxLength) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && (fb.getDocument().getLength() + text.length() - length) <= maxLength) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }
    }

}