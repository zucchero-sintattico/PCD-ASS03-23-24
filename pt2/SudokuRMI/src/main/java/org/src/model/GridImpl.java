package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.Point2d;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class GridImpl implements Grid {

    private static final int GRID_SIZE = 9;
    private List<Cell> cells = new ArrayList<>();

    public GridImpl() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
               this.cells.add(new CellImpl(new Point2d(i, j)));
            }
        }
    }

    public GridImpl(List<Cell> cells) {
        this.cells = new ArrayList<>(cells);
    }

    @Override
    public void addUser(UserImpl users) {

    }

    @Override
    public void removeUser(UserImpl users) {

    }

    @Override
    public synchronized List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    @Override
    public synchronized void updateGrid(List<Cell> cells) {
        this.cells = cells;
    }

    @Override
    public synchronized Cell getCellAt(int row, int col) {
        return this.getCells()
                .stream()
                .filter(cell -> cell.getPosition().getX() == row && cell.getPosition().getY() == col)
                .findFirst().orElseThrow();
    }


    @Override
    public synchronized String print() {
        StringBuilder s = new StringBuilder();
        cells.forEach(e -> {
            if (e.getNumber().isPresent()) {
                s.append(e.getNumber().get());
            } else {
                s.append("-");
            }
            s.append("  ");
            if (e.getPosition().getY() == GRID_SIZE - 1) {
                s.append("\n");
            }
        });
        cells.forEach(e -> {
            if (e.isSelected().isPresent()) {
                s.append(e.isSelected().get().getName()).append(" -> ");
                s.append(e.getPosition().getX());
                s.append(",");
                s.append(e.getPosition().getY());
                s.append("\n");
            }
        });
        return s.toString();
    }

}
