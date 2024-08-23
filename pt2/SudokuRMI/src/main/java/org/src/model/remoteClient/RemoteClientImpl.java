package org.src.model.remoteClient;
import org.src.model.grid.SudokuGrid;

import java.rmi.RemoteException;
import java.util.function.Consumer;

public class RemoteClientImpl implements RemoteClient {

    private Consumer<SudokuGrid> updateGridHandle = (SudokuGrid grid) -> {};

    public RemoteClientImpl() {}

    @Override
    public void updateGrid(SudokuGrid grid) throws RemoteException {
        this.updateGridHandle.accept(grid);
    }

    @Override
    public void setOnUpdateGridHandler(Consumer<SudokuGrid> updateGridHandle) throws RemoteException {
        this.updateGridHandle = updateGridHandle;
    }

}
