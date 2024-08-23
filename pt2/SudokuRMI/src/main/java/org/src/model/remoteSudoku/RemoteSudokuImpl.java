package org.src.model.remoteSudoku;

import org.src.common.Point2d;
import org.src.model.grid.SudokuGrid;
import org.src.model.grid.SudokuFactory;
import org.src.model.user.RemoteUser;
import org.src.model.user.UserDataImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RemoteSudokuImpl implements RemoteSudoku {

    //TODO example class, check everything (Also Interface)

    private SudokuGrid grid = SudokuFactory.createGrid();
    private final Map<String, RemoteUser> users = new HashMap<>();
    private final Registry registry = LocateRegistry.getRegistry();

    public RemoteSudokuImpl() throws RemoteException {}

    @Override
    public void addUser(String userId) throws RemoteException, NotBoundException {
        RemoteUser remoteUser = (RemoteUser) registry.lookup(userId);
        this.users.put(userId, remoteUser);
    }

    @Override
    public synchronized void removeUser(String userId) throws RemoteException {
        this.users.remove(userId);
    }

    @Override
    public synchronized void selectCell(String username, Point2d position) throws RemoteException {
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

    @Override
    public synchronized void test(SudokuGrid grid) throws RemoteException {
        System.out.println(grid);
    }

    private void sendUpdate() {
        this.users.forEach((userId, user) -> {
            try {
                user.updateGrid(this.grid);
            } catch (RemoteException e) {
                try {
                    this.removeUser(userId);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

}