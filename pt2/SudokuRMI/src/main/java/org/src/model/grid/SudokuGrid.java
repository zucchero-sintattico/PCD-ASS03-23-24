package org.src.model.grid;

import org.src.model.grid.cell.Cell;

import java.util.List;

public class SudokuGrid implements Grid {

    private final Grid sudokuGrid;

    public SudokuGrid(List<Cell> grid) throws IllegalArgumentException {
        this.validateInput(grid);
        this.sudokuGrid = new GridImpl(grid);
    }

    private void validateInput(List<Cell> grid) throws IllegalArgumentException {
        //TODO implement
    }

    @Override
    public List<Cell> cells() {
        return sudokuGrid.cells();
    }

    @Override
    public Cell cellAt(int row, int col) {
        return sudokuGrid.cellAt(row, col);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

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
        this.sudokuGrid.cells().forEach(cell -> {
            if (cell.user().isPresent()) {
                s.append(cell.user().get().name()).append(" -> ");
                s.append(cell.position().x());
                s.append(",");
                s.append(cell.position().y());
                s.append("\n");
            }
        });


        return s.toString();
    }
}
