package server.remoteSudoku;

import client.logic.remoteClient.RemoteClient;
import common.Point2d;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSudoku extends Remote {

    void addClient(String username, RemoteClient clientStub) throws RemoteException, NotBoundException, IllegalArgumentException;

    void removeClient(String username) throws RemoteException;

    void selectCell(String userId, Point2d position) throws RemoteException, IllegalArgumentException;

    void updateCellNumber(String userId, Point2d position, int number) throws RemoteException, IllegalArgumentException;

}