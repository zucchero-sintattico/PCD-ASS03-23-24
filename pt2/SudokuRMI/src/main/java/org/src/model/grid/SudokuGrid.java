package org.src.model.grid;

import org.src.model.grid.cell.Cell;

import java.io.Serializable;
import java.util.List;

public interface SudokuGrid extends Serializable {

    List<Cell> cells();

    boolean won();

}
