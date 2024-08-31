package client;

import client.logic.Controller;
import client.logic.ControllerImpl;
import client.view.SudokuView;
import client.view.SudokuViewImpl;

public class RunApp {

    public static void main(String[] args) {
        Controller controller = new ControllerImpl();
        SudokuView view = new SudokuViewImpl(controller);
        controller.setView(view);
        view.display();
    }

}