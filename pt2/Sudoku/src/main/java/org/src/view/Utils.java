package org.src.view;

import javax.swing.*;
import java.awt.*;

public class Utils {

    private static String username = "";

    public static void showErrorMessage(JFrame frame, String title, String message){
        JOptionPane.showMessageDialog(frame, title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void showMessage(JFrame frame, String title, String message){
        JOptionPane.showMessageDialog(frame, title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void setUsername(String nickname){
        username = nickname;
        System.out.println("Username: " + nickname);
    }

    public static String getUsername(){
        return username;
    }
}