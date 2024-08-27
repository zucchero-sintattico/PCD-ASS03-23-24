package server.registrationService;

import server.remoteSudoku.RemoteSudokuImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote {

    void registerSudoku(String sudokuId) throws RemoteException, AlreadyBoundException;

    void unRegisterSudoku(String sudokuId) throws RemoteException, NotBoundException;

}
