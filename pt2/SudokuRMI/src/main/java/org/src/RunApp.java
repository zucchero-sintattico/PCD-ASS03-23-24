package org.src;

import org.src.model.Controller;
import org.src.model.ControllerImpl;
import org.src.view.SudokuView;
import org.src.view.SudokuViewImpl;

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