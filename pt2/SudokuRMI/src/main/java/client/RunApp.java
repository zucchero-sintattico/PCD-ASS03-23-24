package client;

import client.model.Controller;
import client.model.ControllerImpl;
import client.view.SudokuView;
import client.view.SudokuViewImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class RunApp {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Controller controller = new ControllerImpl();
        SudokuView view = new SudokuViewImpl(controller);
        controller.setView(view);
        view.display();
    }

}