package org.src.model.user;

import org.src.model.grid.SudokuGrid;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteUser extends Remote {
    //TODO check and maybe refactor/improve
    void updateGrid(SudokuGrid grid) throws RemoteException;

}
