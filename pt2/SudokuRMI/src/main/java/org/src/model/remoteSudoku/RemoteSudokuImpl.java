package org.src.model.remoteSudoku;

import org.src.common.Point2d;
import org.src.model.grid.SudokuGrid;
import org.src.model.grid.SudokuFactory;
import org.src.model.remoteClient.RemoteClient;
import org.src.model.user.UserDataImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteSudokuImpl implements RemoteSudoku {

    private SudokuGrid grid = SudokuFactory.createGrid();
    private final Map<String, RemoteClient> clients = new HashMap<>();
    private final Registry registry = LocateRegistry.getRegistry();

    public RemoteSudokuImpl() throws RemoteException {}

    @Override
    public synchronized void addUser(String username) throws RemoteException, NotBoundException {
        RemoteClient remoteUser = (RemoteClient) registry.lookup(username);
        this.clients.put(username, remoteUser);
        remoteUser.updateGrid(this.grid);
    }

    @Override
    public synchronized void removeUser(String username) throws RemoteException {
        this.clients.remove(username);
        this.removeSelection(username);
        this.sendUpdate();
    }

    private void removeSelection(String username) {
        this.grid = SudokuFactory.createGrid(this.grid.cells().stream().map(cell -> {
            if (cell.user().isPresent() && cell.user().get().name().equals(username)) {
                return cell.removeUser();
            }
            return cell;
        }).toList());
    }

    @Override
    public synchronized void selectCell(String username, Point2d position) throws RemoteException {
        this.removeSelection(username);
        this.grid = SudokuFactory.createGrid(this.grid.cells().stream().map(cell -> {
            if (cell.position().equals(position)) {
                return cell.setUser(new UserDataImpl(username));
            }
            return cell;
        }).toList());
        this.sendUpdate();
    }

    @Override
    public synchronized void updateCell(String username, Point2d position, int number) throws RemoteException {
        this.grid = SudokuFactory.createGrid(this.grid.cells().stream().map(cell -> {
            if (cell.position().equals(position) && cell.user().isPresent()){
                if(cell.user().get().name().equals(username)){
                    return cell.setNumber(number).removeUser();
                }
            }
            return cell;
        }).toList());
        this.sendUpdate();
    }

    private void sendUpdate() {
        Map<String, RemoteClient> unresponsiveClient = tryToUpdateClients();
        if(!unresponsiveClient.isEmpty()){
            unresponsiveClient.forEach(this.clients::remove);
            unresponsiveClient.keySet().forEach(this::removeSelection);
            sendUpdate();
        }
    }

    private Map<String, RemoteClient> tryToUpdateClients() {
        Map<String, RemoteClient> unresponsiveClient = new HashMap<>();
        this.clients.forEach((username, client) -> {
            try {
                client.updateGrid(this.grid);
            } catch (RemoteException e) {
                unresponsiveClient.put(username,client);
            }
        });
        return unresponsiveClient;
    }

}