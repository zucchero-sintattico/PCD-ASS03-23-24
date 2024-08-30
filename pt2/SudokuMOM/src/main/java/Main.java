import logic.Controller;
import logic.ControllerImpl;
import view.*;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        ScreenManager screenManager = new ScreenManager();
        Controller controller = new ControllerImpl();
        JFrame loginView = new LoginView(screenManager, controller);
        JFrame menuView = new MenuView(screenManager);
        JFrame joinGameView = new JoinGameView(screenManager, controller);
        JFrame newGameView = new NewGameView(screenManager, controller);
        SudokuGridView gridView = new SudokuGridView(screenManager, controller);

        screenManager.addScreen(Screen.LOGIN, loginView);
        screenManager.addScreen(Screen.MENU, menuView);
        screenManager.addScreen(Screen.NEW_GAME, newGameView);
        screenManager.addScreen(Screen.JOIN_GAME, joinGameView);
        screenManager.addScreen(Screen.GRID, gridView);

        controller.setView(gridView);
        screenManager.switchScreen(Screen.LOGIN);
    }
}