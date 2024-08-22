package org.src.model;

import org.src.common.RemoteSudoku;
import org.src.common.User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RemoteSudokuImpl implements RemoteSudoku {

    private Grid grid = SudokuFactory.createGrid();
    private final Map<String, User> users = new HashMap<>();
    private final Registry registry = LocateRegistry.getRegistry();

    public RemoteSudokuImpl() throws RemoteException {}

    @Override
    public void addUser(String userId) throws RemoteException, NotBoundException {
        User remoteUser = (User) registry.lookup(userId);
        users.put(userId, remoteUser);
    }

    @Override
    public void removeUser(String userId) throws RemoteException {
        users.remove(userId);
    }

//    private void sendUpdate() {
//        users.forEach((userId, user) -> {
//            try {
//                //todo ??? -> user.updateGrid(grid.getCells());
//            } catch (RemoteException e) {
//                try {
//                    removeUser(userId);
//                } catch (RemoteException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });
//    }
}

