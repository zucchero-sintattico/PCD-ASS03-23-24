package org.src.model;

import org.src.common.Point2d;
import org.src.view.SudokuView;

public interface Controller {

    void createSudoku(String username, String sudokuId);

    void joinSudoku(String username, String sudokuId);

    String getUsername();

    void leaveSudoku();

    void selectCell(Point2d cellPosition);

    void updateCellNumber(Point2d cellPosition, int number);

    void setView(SudokuView view);

}