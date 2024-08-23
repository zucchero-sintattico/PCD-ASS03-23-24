package org.src.model.grid;

import org.src.common.Point2d;
import org.src.model.grid.cell.Cell;
import org.src.model.grid.cell.CellImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridImpl implements Grid {

    private final List<Cell> cells;

    public GridImpl(List<Cell> cells) {
        this.cells = cells;
    }

    public GridImpl(int size) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells.add(new CellImpl(new Point2d(i, j)));
            }
        }
        this.cells = cells;
    }

    @Override
    public List<Cell> cells() {
        return Collections.unmodifiableList(cells);
    }

}