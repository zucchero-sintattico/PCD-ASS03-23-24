package org.src.controller;

import org.src.common.Grid;
import org.src.common.User;
import org.src.view.SudokuView;

import java.io.IOException;

public interface Controller {
    void createSudoku(String username) throws IOException;
    void joinSudoku(String username, String sudokuId) throws IOException;
    User getUser();
    void setUser(User user);
    void setGridId(String gridId);
    String getGridId();
    void selectCell(Grid grid, User user, int row, int col) throws IOException;
    void makeMove(Grid grid, User user, int number) throws IOException;
    void setView(SudokuView view);
}
