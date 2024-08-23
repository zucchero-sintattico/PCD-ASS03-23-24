package org.src.model;

import org.src.common.Point2d;
import org.src.view.SudokuView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface Controller {

    void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException;

    void joinSudoku(String username, String sudokuId) throws NotBoundException, RemoteException;

    String getUsername() throws RemoteException;

    void leaveSudoku();

    void selectCell(Point2d cellPosition) throws RemoteException;

    void updateCellNumber(Point2d cellPosition, int number);

    void setView(SudokuView view);

    void deselectCell(Point2d cellPosition) throws RemoteException;
}