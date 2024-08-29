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
        JFrame login = new LoginView(screenManager, controller);
        JFrame menu = new MenuView(screenManager);
        JFrame joinGameView = new JoinGameView(screenManager, controller);
        JFrame newGameView = new NewGameView(screenManager, controller);
        SudokuGridView view = new SudokuGridView(screenManager, controller);

        screenManager.addScreen(Screen.LOGIN, login);
        screenManager.addScreen(Screen.MENU, menu);
        screenManager.addScreen(Screen.NEW_GAME, newGameView);
        screenManager.addScreen(Screen.JOIN_GAME, joinGameView);
        screenManager.addScreen(Screen.GRID, view);

        controller.setView(view);
        screenManager.switchScreen(Screen.LOGIN);
    }
}
