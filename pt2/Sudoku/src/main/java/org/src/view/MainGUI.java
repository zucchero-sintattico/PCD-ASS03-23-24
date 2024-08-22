package org.src.view;

import javax.swing.*;

public class MainGUI {

    public static void main(String[] args) {
        Login login = new Login();
        SwingUtilities.invokeLater(login::display);
    }
}
