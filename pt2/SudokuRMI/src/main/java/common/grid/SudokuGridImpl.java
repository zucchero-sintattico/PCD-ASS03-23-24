package common.grid;

import common.grid.cell.Cell;

import java.util.Collections;
import java.util.List;

public record SudokuGridImpl(List<Cell> cells) implements SudokuGrid {

    public SudokuGridImpl {
        if (!SudokuFactory.validateSudoku(cells)) throw new IllegalArgumentException("Invalid input");
        cells = Collections.unmodifiableList(cells);
    }

    @Override
    public boolean won() {
        return this.cells.stream().allMatch(cell -> cell.number().isPresent());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        this.buildGrid(s);
        this.buildSelectedCellsInfo(s);
        return s.toString();
    }

    private void buildSelectedCellsInfo(StringBuilder s) {
        this.cells.forEach(cell -> {
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
        this.cells.forEach(cell -> {
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