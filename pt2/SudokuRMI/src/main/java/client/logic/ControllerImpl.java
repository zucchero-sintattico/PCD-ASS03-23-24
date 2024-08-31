package client.logic;

import common.grid.SudokuGrid;
import server.RunRegistrationService;
import common.Point2d;
import server.registrationService.RegistrationService;
import server.remoteSudoku.RemoteSudoku;
import client.logic.remoteClient.RemoteClient;
import client.logic.remoteClient.RemoteClientImpl;
import client.view.SudokuView;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

public class ControllerImpl implements Controller {

    private String username;
    private String sudokuId;
    private SudokuView view;
    private RemoteSudoku remoteSudoku;

    @Override
    public void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException, AlreadyBoundException, IllegalArgumentException {
        RegistrationService registrationService = (RegistrationService) LocateRegistry.getRegistry().lookup(RunRegistrationService.REGISTRATION_SERVICE_NAME);
        registrationService.registerSudoku(sudokuId);
        this.joinSudoku(username, sudokuId);
    }

    @Override
    public void joinSudoku(String username, String sudokuId) throws RemoteException, IllegalArgumentException {
        this.username = username;
        this.sudokuId = sudokuId;
        Consumer<SudokuGrid> updateHandle = this.view != null ? this.view::update : (sudokuGrid) -> {};
        RemoteClient remoteClient = new RemoteClientImpl(updateHandle);
        RemoteClient clientStub = (RemoteClient) UnicastRemoteObject.exportObject(remoteClient, 0);
        try {
            this.remoteSudoku = (RemoteSudoku) LocateRegistry.getRegistry().lookup(sudokuId);
            this.remoteSudoku.addUser(username, clientStub);
        } catch (NotBoundException e) {
            throw new IllegalArgumentException("This sudoku doesn't exist");
        }
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getSudokuId() {
        return this.sudokuId;
    }

    @Override
    public void leaveSudoku() throws RemoteException {
        this.remoteSudoku.removeUser(this.getUsername());
    }

    @Override
    public void selectCell(Point2d cellPosition) throws RemoteException, IllegalArgumentException {
        this.remoteSudoku.selectCell(this.getUsername(), cellPosition);
    }

    @Override
    public void updateCellNumber(Point2d cellPosition, int number) throws RemoteException, IllegalArgumentException {
        this.remoteSudoku.updateCellNumber(this.getUsername(), cellPosition, number);
    }

    @Override
    public void setView(SudokuView view) {
        this.view = view;
    }

}