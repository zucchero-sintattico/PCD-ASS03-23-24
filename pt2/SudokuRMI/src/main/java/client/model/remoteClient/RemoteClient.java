package client.model.remoteClient;

import common.grid.SudokuGrid;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Consumer;

public interface RemoteClient extends Remote {

    void updateGrid(SudokuGrid grid) throws RemoteException;

}
