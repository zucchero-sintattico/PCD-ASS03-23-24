package org.src.model.remoteClient;

import org.src.model.grid.SudokuGrid;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Consumer;

public interface RemoteClient extends Remote {

    void updateGrid(SudokuGrid grid) throws RemoteException;

    void setOnUpdateGridHandler(Consumer<SudokuGrid> updateGridHandle) throws RemoteException;

}
