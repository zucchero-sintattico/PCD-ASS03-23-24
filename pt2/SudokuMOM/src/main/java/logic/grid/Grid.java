package logic.grid;

import logic.grid.cell.Cell;

import java.util.List;

public interface Grid {

    List<Cell> getCells();
    
    Cell getCellAt(int row, int col);

    void checkAndUpdateGrid(List<Cell> newCellList) throws IllegalArgumentException;

    boolean haveWon();

}