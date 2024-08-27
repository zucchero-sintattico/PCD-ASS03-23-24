package org.src.view;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MainGUI {

    public static void main(String[] args) throws IOException, TimeoutException {
        ScreenManager screenManager = new ScreenManager();

        JFrame login = new Login(screenManager);
        JFrame menu = new Menu(screenManager);
        JFrame solveMenu = new SolveMenu(screenManager);

        screenManager.addScreen("login", login);
        screenManager.addScreen("menu", menu);
        screenManager.addScreen("solveMenu", solveMenu);

        screenManager.switchScreen("login");
    }
}
