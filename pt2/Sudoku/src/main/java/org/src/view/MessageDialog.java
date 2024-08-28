package org.src.view;

import javax.swing.*;

public class MessageDialog {

    public static void showErrorMessage(JFrame frame, String title, String message){
        JOptionPane.showMessageDialog(frame, title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void showMessage(JFrame frame, String title, String message){
        JOptionPane.showMessageDialog(frame, title, message, JOptionPane.INFORMATION_MESSAGE);
    }

}