package org.src.model;

import org.src.common.Point2d;
import org.src.model.grid.SudokuFactory;
import org.src.view.SudokuView;

public class ControllerImpl implements Controller {

    private SudokuView view;

    @Override
    public void createSudoku(String username, String sudokuId) {
        if (view != null) {
            view.init(SudokuFactory.createGrid());
        }
    }

    @Override
    public void joinSudoku(String username, String sudokuId) {

    }

    @Override
    public String getUsername() {
        return "testuser";
    }
    

    @Override
    public void leaveSudoku() {

    }

    @Override
    public void selectCell(Point2d cellPosition) {

    }

    @Override
    public void updateCellNumber(Point2d cellPosition, int number) {

    }

    @Override
    public void setView(SudokuView view) {
        this.view = view;
    }

}
