package server.registrationService;

import server.remoteSudoku.RemoteSudoku;
import server.remoteSudoku.RemoteSudokuImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RegistrationServiceImpl implements RegistrationService {

    @Override
    public void registerSudoku(String sudokuId) throws RemoteException, AlreadyBoundException {
        Registry registry = LocateRegistry.getRegistry();
        RemoteSudoku remoteSudoku = new RemoteSudokuImpl(sudokuId, this::unRegisterSudoku);
        RemoteSudoku stub = (RemoteSudoku) UnicastRemoteObject.exportObject(remoteSudoku, 0);
        registry.bind(sudokuId, stub);
    }

    private void unRegisterSudoku(String sudokuId) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            registry.unbind(sudokuId);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

}
