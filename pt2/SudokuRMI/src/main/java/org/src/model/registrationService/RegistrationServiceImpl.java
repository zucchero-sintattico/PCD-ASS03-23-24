package org.src.model.registrationService;

import org.src.model.remoteSudoku.RemoteSudoku;
import org.src.model.remoteSudoku.RemoteSudokuImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RegistrationServiceImpl implements RegistrationService {

    @Override
    public void registerSudoku(String sudokuId) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        RemoteSudoku remoteSudoku = new RemoteSudokuImpl();
        RemoteSudoku stub = (RemoteSudoku) UnicastRemoteObject.exportObject(remoteSudoku, 0);
        registry.rebind(sudokuId, stub);
    }
}
