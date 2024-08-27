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
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

public class ControllerImpl implements Controller {

    private String username;
    private SudokuView view;
    private RemoteSudoku remoteSudoku;
    private final Registry registry;
    private final RegistrationService registrationService;

    public ControllerImpl() throws RemoteException, NotBoundException {
        this.registry = LocateRegistry.getRegistry();
        this.registrationService = (RegistrationService) registry.lookup(RunRegistrationService.REGISTRATION_SERVICE_NAME);
    }

    @Override
    public void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException, AlreadyBoundException, IllegalArgumentException {
        this.registrationService.registerSudoku(sudokuId);
        this.joinSudoku(username, sudokuId);
    }

    @Override
    public void joinSudoku(String username, String sudokuId) throws RemoteException, NotBoundException, IllegalArgumentException {
        this.username = username;
//    NB: using methodReference instead of lambda
//    remoteClient bind to the current instance of
//    this.view avoiding concurrency problem
//    so this.setView(SudokuView view) call doesn't affect updateHandle reference/behavior
//
//    private void update(SudokuGrid sudokuGrid){
//        if(this.view != null){
//            this.view.update(sudokuGrid);
//        }
//    }
//
//    For dynamic binding solution updateHandle could be bind to this.update(SudokuGrid grid)
//    and both this::setView and this::update must be synchronized.
//
//    However, considering the current use case, it's ok also to leave this::setView not synchronized
//    and define a lambda instead of this::update
        Consumer<SudokuGrid> updateHandle = this.view != null ? this.view::update : (sudokuGrid) -> {};
        RemoteClient remoteClient = new RemoteClientImpl(updateHandle);
        RemoteClient clientStub = (RemoteClient) UnicastRemoteObject.exportObject(remoteClient, 0);
        registry.rebind(username, clientStub);
        try {
            this.remoteSudoku = (RemoteSudoku) registry.lookup(sudokuId);
        } catch (NotBoundException e) {
            throw new IllegalArgumentException("This sudoku doesn't exist");
        }
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