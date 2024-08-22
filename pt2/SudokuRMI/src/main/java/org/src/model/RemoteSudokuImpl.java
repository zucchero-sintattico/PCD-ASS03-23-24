package org.src.model;


import org.src.model.grid.SudokuGrid;
import org.src.model.grid.Grid;
import org.src.model.grid.SudokuFactory;
import org.src.model.user.RemoteUser;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RemoteSudokuImpl implements RemoteSudoku {

    private Grid grid = SudokuFactory.createGrid();
    private final Map<String, RemoteUser> users = new HashMap<>();
    private final Registry registry = LocateRegistry.getRegistry();

    public RemoteSudokuImpl() throws RemoteException {}

    @Override
    public void addUser(String userId) throws RemoteException, NotBoundException {
        RemoteUser remoteUser = (RemoteUser) registry.lookup(userId);
        users.put(userId, remoteUser);
    }

    @Override
    public void removeUser(String userId) throws RemoteException {
        users.remove(userId);
    }

    @Override
    public void test(SudokuGrid grid) throws RemoteException {

        System.out.println(grid);
    }

//    private void sendUpdate() {
//        users.forEach((userId, user) -> {
//            try {
//                //todo ??? -> user.updateGrid(grid);
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

