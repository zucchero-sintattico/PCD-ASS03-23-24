package org.src;

import org.src.controller.Controller;
import org.src.controller.ControllerImpl;
import org.src.view.*;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        ScreenManager screenManager = new ScreenManager();
        Controller controller = new ControllerImpl();
        JFrame login = new Login(screenManager, controller);
        JFrame menu = new Menu(screenManager);
        JFrame solveMenu = new JoinGameView(screenManager, controller);
        JFrame gridId = new NewGameView(screenManager, controller);
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
