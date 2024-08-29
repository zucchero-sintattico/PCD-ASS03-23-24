package view;

import logic.Controller;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public abstract class AbstractInputView extends JFrame {
    protected final JLabel label;
    protected final JTextField inputField = new JTextField(15);
    protected final JButton actionButton;
    protected final Controller controller;
    protected final ScreenManager screenManager;

    public AbstractInputView(ScreenManager screenManager, Controller controller, String title, String textLabel, String buttonText) {
        this.screenManager = screenManager;
        this.controller = controller;
        this.label = new JLabel(textLabel);
        this.actionButton = new JButton(buttonText);
        this.buildFrame(title);
        this.buildComponents();
        this.addComponentsInFrame();
        this.attachListener();
        this.spawnFrameAtCenter();
    }

    private void buildFrame(String title) {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
    }

    private void buildComponents() {
        Font arial = new Font("Arial", Font.PLAIN, 16);
        Font arialBold = new Font("Arial", Font.BOLD, 16);
        this.label.setFont(arialBold);
        this.inputField.setFont(arial);
        this.actionButton.setFont(arial);
        ((AbstractDocument) this.inputField.getDocument()).setDocumentFilter(new LengthFilter(14));
    }

    private void addComponentsInFrame() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        this.add(label, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        this.add(inputField, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(actionButton, gridBagConstraints);
    }

    private void spawnFrameAtCenter() {
        this.setLocationRelativeTo(null);
    }


    private void attachListener() {
        this.actionButton.addActionListener(e -> {
            if (inputField.getText().isEmpty()) {
                MessageDialog.showErrorMessage(this, "Error", "Invalid input");
            } else {
                handleAction();
            }
        });
    }

    protected abstract void handleAction();

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