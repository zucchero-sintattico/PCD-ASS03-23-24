package client.logic.remoteClient;

import common.grid.SudokuGrid;

import java.rmi.RemoteException;
import java.util.function.Consumer;

public class RemoteClientImpl implements RemoteClient {

    private final Consumer<SudokuGrid> updateGridHandle;

    public RemoteClientImpl(Consumer<SudokuGrid> updateGridHandle) {
        this.updateGridHandle = updateGridHandle;
    }

    @Override
    public void updateGrid(SudokuGrid grid) throws RemoteException {
        this.updateGridHandle.accept(grid);
    }

}