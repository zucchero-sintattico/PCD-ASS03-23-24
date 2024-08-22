package org.src;

import org.src.common.Point2d;
import org.src.model.Controller;
import org.src.model.ControllerImpl;
import org.src.model.RemoteSudoku;
import org.src.model.RemoteSudokuImpl;
import org.src.model.grid.cell.CellImpl;
import org.src.model.grid.SudokuFactory;
import org.src.view.SudokuView;
import org.src.view.SudokuViewImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Controller controller = new ControllerImpl();
        SudokuView view = new SudokuViewImpl(controller);
        controller.setView(view);
        view.display();
        System.out.println(SudokuFactory.createGrid());

        String gridID = "1";

        RemoteSudoku sudoku = new RemoteSudokuImpl();
        RemoteSudoku sudokuStub = (RemoteSudoku) UnicastRemoteObject.exportObject(sudoku, 0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(gridID, sudokuStub);

        RemoteSudoku remoteGrid = (RemoteSudoku) registry.lookup(gridID);
        remoteGrid.test(SudokuFactory.createGrid());


    }
}
