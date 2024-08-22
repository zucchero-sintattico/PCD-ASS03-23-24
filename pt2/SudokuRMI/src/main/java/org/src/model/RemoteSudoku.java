package org.src.model;


import org.src.model.grid.SudokuGrid;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSudoku extends Remote {

    void addUser(String userId) throws RemoteException, NotBoundException;
    void removeUser(String userId) throws RemoteException;
    void test(SudokuGrid grid) throws RemoteException;
}
