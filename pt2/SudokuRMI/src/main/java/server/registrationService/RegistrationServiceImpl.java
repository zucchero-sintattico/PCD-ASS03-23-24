package server.registrationService;

import server.remoteSudoku.RemoteSudoku;
import server.remoteSudoku.RemoteSudokuImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

public class RegistrationServiceImpl implements RegistrationService {

    private final HashSet<String> sudokuIds = new HashSet<>();

    @Override
    public synchronized void registerSudoku(String sudokuId) throws RemoteException, AlreadyBoundException {
        if(this.sudokuIds.contains(sudokuId)) {
            throw new AlreadyBoundException("Sudoku with id " + sudokuId + " already exists");
        }
        Registry registry = LocateRegistry.getRegistry();
        RemoteSudoku remoteSudoku = new RemoteSudokuImpl(sudokuId, this::unRegisterSudoku);
        RemoteSudoku stub = (RemoteSudoku) UnicastRemoteObject.exportObject(remoteSudoku, 0);
        this.sudokuIds.add(sudokuId);
        registry.rebind(sudokuId, stub);
    }

    private synchronized void unRegisterSudoku(String sudokuId) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            this.sudokuIds.remove(sudokuId);
            registry.unbind(sudokuId);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

}