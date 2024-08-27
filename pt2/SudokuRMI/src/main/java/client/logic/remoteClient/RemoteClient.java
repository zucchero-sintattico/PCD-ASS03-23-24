package client.logic.remoteClient;

import common.grid.SudokuGrid;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {

    void updateGrid(SudokuGrid grid) throws RemoteException;

}
