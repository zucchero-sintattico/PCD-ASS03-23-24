package org.src.model;

import org.src.common.Point2d;
import org.src.model.grid.SudokuFactory;
import org.src.view.SudokuView;

public class ControllerImpl implements Controller {

    //TODO example class, check everything (Interface should be OK)

    private SudokuView view;
    private String username;

    @Override
    public void createSudoku(String username, String sudokuId) {
        this.joinSudoku(username, sudokuId);
    }

    @Override
    public void joinSudoku(String username, String sudokuId) {
        this.username = username;
        if (view != null) {
            view.update(SudokuFactory.createGrid());
        }
    }

    @Override
    public String getUsername() {
        return this.username;
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