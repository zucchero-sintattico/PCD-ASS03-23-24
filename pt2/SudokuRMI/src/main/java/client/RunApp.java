package client;

import client.logic.Controller;
import client.logic.ControllerImpl;
import client.view.SudokuView;
import client.view.SudokuViewImpl;
import common.Point2d;
import common.grid.cell.Cell;
import common.grid.cell.CellImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RunApp {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Controller controller = new ControllerImpl();
        SudokuView view = new SudokuViewImpl(controller);
        controller.setView(view);
        view.display();
    }

}