package client.model;

import common.Point2d;
import client.view.SudokuView;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface Controller {

    void createSudoku(String username, String sudokuId) throws RemoteException, NotBoundException, AlreadyBoundException;

    void joinSudoku(String username, String sudokuId) throws RemoteException, NotBoundException;

    String getUsername();

    void leaveSudoku() throws RemoteException, NotBoundException;

    void selectCell(Point2d cellPosition) throws RemoteException, IllegalArgumentException;

    void updateCellNumber(Point2d cellPosition, int number) throws RemoteException, IllegalArgumentException;

    void setView(SudokuView view);

}