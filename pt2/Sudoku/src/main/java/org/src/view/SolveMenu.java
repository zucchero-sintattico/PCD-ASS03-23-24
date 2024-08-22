package org.src.view;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.Objects;

public class SolveMenu extends JFrame {

    private final JLabel label = new JLabel("Insert session id");
    private final JTextField sessionId = new JTextField(15);
    private final JButton joinSession = new JButton("Join session");

    public SolveMenu(){
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
        this.add(sessionId, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(joinSession, gridBagConstraints);
    }

    private void buildComponents() {
        Font arial = new Font("Arial", Font.PLAIN, 16);
        Font arialBold = new Font("Arial", Font.BOLD, 16);
        this.label.setFont(arialBold);
        this.sessionId.setFont(arial);
        this.joinSession.setFont(arial);
        ((AbstractDocument) this.sessionId.getDocument()).setDocumentFilter(new LengthFilter(14));
    }

    private void spawnFrameAtCenter(){
        this.setLocation(Utils.computeCenteredXDimension(this.getWidth()), Utils.computeCenteredYDimension(this.getHeight()));
    }

    private void buildFrame() {
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
    }

    public void display(){
        this.setVisible(true);
    }

    private void attachListener(){
        this.joinSession.addActionListener(e -> {
            if(Objects.equals(this.sessionId.getText(), "")){
                Utils.showErrorMessage(this, "Invalid Session ID", "Session Problem");
            }else{
                Utils.setUsername(sessionId.getText());
                this.dispose();
                //TODO do something
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