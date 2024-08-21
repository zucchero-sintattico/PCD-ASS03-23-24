package org.src.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Grid extends Remote {

    List<Cell> getCells() throws RemoteException;

    void updateGrid(List<Cell> cells) throws RemoteException;

    Cell getCellAt(int row, int col) throws RemoteException;

    String print() throws RemoteException;

}
