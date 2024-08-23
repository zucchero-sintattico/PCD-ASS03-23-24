package org.src.view;

import org.src.model.grid.SudokuGrid;

public interface SudokuView {

    void update(SudokuGrid grid);

    void display();

}