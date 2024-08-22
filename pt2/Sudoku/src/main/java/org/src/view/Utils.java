package org.src.view;

import javax.swing.*;
import java.awt.*;

public class Utils {

    private static String username = "";
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final Dimension screenSize = toolkit.getScreenSize();

    public static int computeCenteredXDimension(int width) {
        return (screenSize.width - width) / 2;
    }

    public static int computeCenteredYDimension(int height) {
        return (screenSize.height - height) / 2;
    }

    public static void showErrorMessage(JFrame frame, String title, String message){
        JOptionPane.showMessageDialog(frame, title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void setUsername(String nickname){
        username = nickname;
    }

    public static String getUsername(){
        return username;
    }
}