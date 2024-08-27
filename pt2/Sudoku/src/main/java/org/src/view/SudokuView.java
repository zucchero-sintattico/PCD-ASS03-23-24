package org.src.view;

import org.src.common.Grid;

public interface SudokuView {
    void update(Grid grid);
    void haveWon();
    void display();
}
