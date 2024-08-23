package org.src.model.user;

import org.src.model.grid.SudokuGrid;
import org.src.view.SudokuView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteUser extends Remote {
    void updateGrid(SudokuGrid grid) throws RemoteException;

    String getName() throws RemoteException;

    void setView(SudokuView view) throws RemoteException;

}
