package org.src.model;

import org.src.common.Point2d;
import org.src.model.remoteSudoku.RemoteSudoku;
import org.src.model.remoteSudoku.RemoteSudokuImpl;
import org.src.model.remoteClient.RemoteClient;
import org.src.model.remoteClient.RemoteClientImpl;
import org.src.view.SudokuView;
import org.src.view.SudokuViewImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ControllerImpl implements Controller {

    private String username;
    private SudokuView view;
    private RemoteSudoku remoteSudoku;
    private final Registry registry =  LocateRegistry.getRegistry();

    public ControllerImpl() throws RemoteException {}

    @Override
    public void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException {
        RemoteSudoku sudoku = new RemoteSudokuImpl();
        RemoteSudoku sudokuStub = (RemoteSudoku) UnicastRemoteObject.exportObject(sudoku, 0);
        registry.rebind(sudokuId, sudokuStub);
        this.joinSudoku(username, sudokuId);

    }

    @Override
    public void joinSudoku(String username, String sudokuId) throws RemoteException, NotBoundException {
        this.username = username;
        RemoteClient remoteClient = new RemoteClientImpl();
        if(this.view != null) {
            remoteClient.setOnUpdateGridHandler(this.view::update);
        }
        RemoteClient userStub = (RemoteClient) UnicastRemoteObject.exportObject(remoteClient, 0);
        registry.rebind(username, userStub);
        this.remoteSudoku = (RemoteSudoku) registry.lookup(sudokuId);
        this.remoteSudoku.addUser(username);
    }

    @Override
    public String getUsername() {
        return this.username;
    }
    

    @Override
    public void leaveSudoku() throws RemoteException {
        this.remoteSudoku.removeUser(this.getUsername());
    }

    @Override
    public void selectCell(Point2d cellPosition) throws RemoteException {
        this.remoteSudoku.selectCell(this.getUsername(), cellPosition);
    }

    @Override
    public void updateCellNumber(Point2d cellPosition, int number) throws RemoteException {
        this.remoteSudoku.updateCell(this.getUsername(), cellPosition, number);
    }

    @Override
    public void setView(SudokuView view) {
        this.view = view;
    }

}