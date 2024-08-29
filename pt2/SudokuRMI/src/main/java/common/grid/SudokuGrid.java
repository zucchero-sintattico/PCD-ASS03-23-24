package common.grid;

import common.grid.cell.Cell;

import java.io.Serializable;
import java.util.List;

public interface SudokuGrid extends Serializable {

    List<Cell> cells();

    boolean won();

}
