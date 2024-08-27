package server.remoteSudoku;

import common.Point2d;
import common.grid.SudokuGrid;
import common.grid.SudokuFactory;
import client.logic.remoteClient.RemoteClient;
import common.user.UserDataImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RemoteSudokuImpl implements RemoteSudoku {

    private final String sudokuId;
    private final Consumer<String> unbindHandle;
    private SudokuGrid grid = SudokuFactory.createGrid();
    private final Map<String, RemoteClient> clients = new HashMap<>();
    private final Registry registry = LocateRegistry.getRegistry();

    public RemoteSudokuImpl(String sudokuId, Consumer<String> unbindHandle) throws RemoteException {
        this.sudokuId = sudokuId;
        this.unbindHandle = unbindHandle;
    }

    @Override
    public synchronized void addUser(String username) throws RemoteException, NotBoundException {
        RemoteClient remoteUser = (RemoteClient) registry.lookup(username);
        this.clients.put(username, remoteUser);
        remoteUser.updateGrid(this.grid);
    }

    @Override
    public synchronized void removeUser(String username) throws RemoteException {
        this.clients.remove(username);
        if(this.clients.isEmpty()){
            this.unbindHandle.accept(this.sudokuId);
        }else {
            this.removeSelection(username);
            this.sendUpdate();
        }

    }

    @Override
    public synchronized void selectCell(String username, Point2d position) throws RemoteException, IllegalArgumentException {
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
    public synchronized void updateCellNumber(String username, Point2d position, int number) throws RemoteException, IllegalArgumentException {
        this.grid = SudokuFactory.createGrid(this.grid.cells().stream().map(cell -> {
            if (cell.position().equals(position) && cell.user().isPresent()) {
                if (cell.user().get().name().equals(username)) {
                    return cell.setNumber(number).removeUser();
                }
            }
            return cell;
        }).toList());
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

    private void sendUpdate() {
        this.clients.forEach((username, client) -> {
            try {
                client.updateGrid(this.grid);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

}