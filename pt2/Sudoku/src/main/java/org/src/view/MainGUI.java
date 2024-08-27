package org.src.view;

import org.src.controller.Controller;
import org.src.controller.ControllerImpl;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MainGUI {

    public static void main(String[] args) throws IOException, TimeoutException {
        ScreenManager screenManager = new ScreenManager();
        Controller controller = new ControllerImpl();
        JFrame login = new Login(screenManager, controller);
        JFrame menu = new Menu(screenManager);
        JFrame solveMenu = new SolveMenu(screenManager, controller);
        JFrame gridId = new GridIdView(screenManager, controller);
        SudokuGridView view = new SudokuGridView(screenManager, controller);

        screenManager.addScreen("login", login);
        screenManager.addScreen("menu", menu);
        screenManager.addScreen("solveMenu", solveMenu);
        screenManager.addScreen("gridId", gridId);
        screenManager.addScreen("grid", view);

        controller.setView(view);
        screenManager.switchScreen("login");
    }
}
