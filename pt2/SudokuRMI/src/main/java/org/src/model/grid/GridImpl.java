package org.src.model.grid;

import org.src.model.grid.cell.Cell;
import java.util.Collections;
import java.util.List;

public class GridImpl implements Grid {

    private final List<Cell> cells;

    public GridImpl(List<Cell> cells) {
        this.cells = Collections.unmodifiableList(cells);
    }

    @Override
    public List<Cell> cells() {
        return this.cells;
    }

}