package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.Point2d;

import java.util.ArrayList;
import java.util.List;

public class GridImpl implements Grid {

    List<Cell> cells = new ArrayList<>();

    int GRID_SIZE = 9;

    GridImpl() {
        // Initialize the grid with empty cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                cells.add(new CellImpl(new Point2d(i, j)));
            }
        }
    }

    @Override
    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public void updateGrid(List<Cell> cells) {
        this.cells = cells;
    }
}
