package org.src.model.grid;

import org.src.model.grid.cell.Cell;
import java.util.List;

public class SudokuGrid implements Grid {

    private final Grid sudokuGrid;

    public SudokuGrid(List<Cell> grid) throws IllegalArgumentException {
        if (!SudokuFactory.validateSudoku(grid)) throw new IllegalArgumentException("Invalid input");
        this.sudokuGrid = new GridImpl(grid);
    }

    @Override
    public List<Cell> cells() {
        return this.sudokuGrid.cells();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        this.buildGrid(s);
        this.buildSelectedCellsInfo(s);
        return s.toString();
    }

    private void buildSelectedCellsInfo(StringBuilder s) {
        this.sudokuGrid.cells().forEach(cell -> {
            if (cell.user().isPresent()) {
                s.append(cell.user().get().name()).append(" -> ");
                s.append(cell.position().x());
                s.append(",");
                s.append(cell.position().y());
                s.append("\n");
            }
        });
    }

    private void buildGrid(StringBuilder s) {
        this.sudokuGrid.cells().forEach(cell -> {
            if (cell.number().isPresent()) {
                s.append(cell.number().get());
            } else {
                s.append("-");
            }
            s.append("   ");
            if (cell.position().y() == SudokuFactory.GRID_SIZE - 1) {
                s.append("\n");
            }
        });
    }

}