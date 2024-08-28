package org.src.controller;

import org.src.common.Grid;
import org.src.common.User;
import org.src.view.SudokuView;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Controller {

    void createSudoku(String username, String sudokuId) throws IOException;
    void joinSudoku(String username, String sudokuId) throws IOException;
    void leave() throws IOException, TimeoutException;

    void selectCell(int row, int col) throws IOException;
    void makeMove(int number) throws IOException;

    User getUser();
    void setUser(User user);

    String getGridId();

    void setView(SudokuView view);

}
