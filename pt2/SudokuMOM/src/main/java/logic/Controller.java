package logic;

import logic.user.User;
import view.SudokuView;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Controller {

    void createSudoku(String sudokuId) throws IOException;
    void joinSudoku(String sudokuId) throws IOException;
    void leave() throws IOException, TimeoutException, NullPointerException;

    void selectCell(int row, int col) throws IOException, IllegalArgumentException, NullPointerException;
    void makeMove(int number) throws IOException, IllegalArgumentException, NullPointerException;

    User getUser();
    void setUser(User user);

    String getGridId();

    void setView(SudokuView view);

}