package org.src.common;

import org.src.model.UserImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Grid extends Remote {


    void addUser(UserImpl users) throws RemoteException;

    void removeUser(UserImpl users) throws RemoteException;

    List<Cell> getCells() throws RemoteException;

    void updateGrid(List<Cell> cells) throws RemoteException;

    Cell getCellAt(int row, int col) throws RemoteException;

    String print() throws RemoteException;

}
