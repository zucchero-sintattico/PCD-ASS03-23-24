package org.src.model.user;
import org.src.model.grid.SudokuGrid;
import org.src.view.SudokuView;

import java.rmi.RemoteException;

public class RemoteUserImpl implements RemoteUser {
    private final String name;
    private SudokuView view;

    public void setView(SudokuView view) {
        this.view = view;
    }

    public RemoteUserImpl(String name) {
        this.name = name;

    }

    @Override
    public void updateGrid(SudokuGrid grid) throws RemoteException {
        this.view.update(grid);
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }


}
