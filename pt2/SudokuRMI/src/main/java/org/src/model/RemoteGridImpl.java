package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public class RemoteGridImpl implements Grid, Remote {

    private final Grid grid;

    public RemoteGridImpl(Grid grid) throws RemoteException {
        this.grid = new GridImpl(grid.getCells());
    }

    @Override
    public synchronized List<Cell> getCells() throws RemoteException {
        return grid.getCells();
    }

    @Override
    public synchronized void updateGrid(List<Cell> cells) throws RemoteException {
        grid.updateGrid(cells);
    }

    @Override
    public synchronized Cell getCellAt(int row, int col) throws RemoteException {
        return grid.getCellAt(row, col);
    }

    @Override
    public synchronized String print() throws RemoteException {
        return grid.print();
    }
}
