package org.src.view;

import org.src.controller.Controller;
import org.src.model.UserImpl;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class Login extends JFrame {

    private final JLabel label = new JLabel("Insert username");
    private final JTextField username = new JTextField(15);
    private final JButton login = new JButton("Login");
    private final ScreenManager screenManager;
    private final Controller controller;

    public Login(ScreenManager screenManager, Controller controller){
        this.controller = controller;
        this.screenManager = screenManager;
        this.buildFrame();
        this.buildComponents();
        this.addComponentsInFrame();
        this.attachListener();
        this.spawnFrameAtCenter();
    }

    private void addComponentsInFrame() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        this.add(label, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        this.add(username, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(login, gridBagConstraints);
    }

    private void buildComponents() {
        Font arial = new Font("Arial", Font.PLAIN, 16);
        Font arialBold = new Font("Arial", Font.BOLD, 16);
        this.label.setFont(arialBold);
        this.username.setFont(arial);
        this.login.setFont(arial);
        ((AbstractDocument) this.username.getDocument()).setDocumentFilter(new LengthFilter(14));
    }

    private void spawnFrameAtCenter(){
        this.setLocationRelativeTo(null);
    }

    private void buildFrame() {
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
    }

    private void attachListener(){
        this.login.addActionListener(e -> {
            if(!this.username.getText().isEmpty()){
                this.controller.setUser(new UserImpl(this.username.getText()));
                this.screenManager.switchScreen("menu");
            }else{
                Utils.showErrorMessage(this, "Username Invalid", "This username isn't valid");
            }
        });
    }

    /* To limit the len of the textfield */
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