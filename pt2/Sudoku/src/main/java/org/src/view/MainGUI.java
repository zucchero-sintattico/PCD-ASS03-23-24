package org.src.view;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainGUI {

    public static void main(String[] args) throws IOException, TimeoutException {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.display();
        });
    }
}
