import logic.Controller;
import logic.ControllerImpl;
import view.*;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
//        Counter counter = new Counter();
//        new Thread(() ->{
//            for (int i = 0; i < 10; i++) {
//                try {
//                    counter.setValue();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//        int i = 0;
//        while (i < 9000000){
////            Thread.sleep(1);
//            System.out.println("Value "+i+" is "+System.currentTimeMillis()+": " + counter.getValue());
//            i++;
//        }
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
