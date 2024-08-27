package server.remoteSudoku;


import common.Point2d;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSudoku extends Remote {

    void addUser(String userId) throws RemoteException, NotBoundException;

    void removeUser(String userId) throws RemoteException, NotBoundException;

    void selectCell(String userId, Point2d position) throws RemoteException, IllegalArgumentException;

    void updateCellNumber(String userId, Point2d position, int number) throws RemoteException, IllegalArgumentException;

}
