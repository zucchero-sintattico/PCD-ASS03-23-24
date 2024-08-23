package org.src;

import org.src.model.Controller;
import org.src.model.ControllerImpl;
import org.src.model.remoteSudoku.RemoteSudoku;
import org.src.model.remoteSudoku.RemoteSudokuImpl;
import org.src.model.grid.SudokuFactory;
import org.src.view.SudokuView;
import org.src.view.SudokuViewImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class Main {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Controller controller = new ControllerImpl();
        SudokuView view = new SudokuViewImpl(controller);
        controller.setView(view);
        view.display();

        //-----Test Code-----
        System.out.println(SudokuFactory.createGrid());

        String gridID = "1";

    }
}