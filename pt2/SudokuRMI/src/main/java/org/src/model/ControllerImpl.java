package org.src.model;

import org.src.RunRegistrationService;
import org.src.common.Point2d;
import org.src.model.registrationService.RegistrationService;
import org.src.model.remoteSudoku.RemoteSudoku;
import org.src.model.remoteSudoku.RemoteSudokuImpl;
import org.src.model.remoteClient.RemoteClient;
import org.src.model.remoteClient.RemoteClientImpl;
import org.src.view.SudokuView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
    public void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException {
        this.registrationService.registerSudoku(sudokuId);
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