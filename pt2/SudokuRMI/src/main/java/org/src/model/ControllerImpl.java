package org.src.model;

import org.src.common.Point2d;
import org.src.model.remoteSudoku.RemoteSudoku;
import org.src.model.remoteSudoku.RemoteSudokuImpl;
import org.src.model.user.RemoteUser;
import org.src.model.user.RemoteUserImpl;
import org.src.view.SudokuView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ControllerImpl implements Controller {

    //TODO example class, check everything (Interface should be OK)

    private SudokuView view;
    private RemoteSudoku remoteSudoku;
    private final Registry registry =  LocateRegistry.getRegistry();
    private RemoteUser remoteUser;

    public ControllerImpl() throws RemoteException {
    }

    @Override
    public void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException {
        RemoteSudoku sudoku = new RemoteSudokuImpl();
        RemoteSudoku sudokuStub = (RemoteSudoku) UnicastRemoteObject.exportObject(sudoku, 0);
        registry.rebind(sudokuId, sudokuStub);
        this.joinSudoku(username, sudokuId);

    }

    @Override
    public void joinSudoku(String username, String sudokuId) throws NotBoundException, RemoteException {
        this.remoteUser = new RemoteUserImpl(username);
        if(this.view != null) {
            this.remoteUser.setView(this.view);
        }
        RemoteUser userStub = (RemoteUser) UnicastRemoteObject.exportObject(this.remoteUser, 0);
        registry.rebind(username, userStub);
        this.remoteSudoku = (RemoteSudoku) registry.lookup(sudokuId);
        this.remoteSudoku.addUser(username);
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.remoteUser.getName();
    }
    

    @Override
    public void leaveSudoku() {
        try {
            this.remoteSudoku.removeUser(this.getUsername());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectCell(Point2d cellPosition) throws RemoteException {
        this.remoteSudoku.selectCell(this.getUsername(), cellPosition);
    }

    @Override
    public void updateCellNumber(Point2d cellPosition, int number) {
        try {
            this.remoteSudoku.updateCell(this.getUsername(), cellPosition, number);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setView(SudokuView view) {
        this.view = view;
    }

    @Override
    public void deselectCell(Point2d cellPosition) throws RemoteException {
        this.remoteSudoku.deselectCell(this.getUsername(), cellPosition);
    }

}