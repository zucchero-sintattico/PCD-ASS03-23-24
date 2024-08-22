package org.src.model;


import org.src.common.Point2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


public record Grid(List<Cell> cells){
    public Grid {
        cells = Collections.unmodifiableList(cells);
    }
    public Grid(int size) {
        this(initGrid(size));
    }

    public Cell cellAt(int row, int col) {
        return this.cells
                .stream()
                .filter(cell -> cell.position().x() == row && cell.position().y() == col)
                .findFirst().orElseThrow();
    }

    public String print() {
        List<Cell> sortedCells = sort(new ArrayList<>(cells));
        int gridSize = cells.stream().map(cell -> cell.position().y()).max(Integer::compareTo).orElseThrow();
        StringBuilder s = new StringBuilder();
        sortedCells.forEach(e -> {
            if (e.number().isPresent()) {
                s.append(e.number().get());
            } else {
                s.append("-");
            }
            s.append("  ");
            if (e.position().y() == gridSize) {
                s.append("\n");
            }
        });
        cells.forEach(e -> {
            if (e.user().isPresent()) {
                s.append(e.user().get().getName()).append(" -> ");
                s.append(e.position().x());
                s.append(",");
                s.append(e.position().y());
                s.append("\n");
            }
        });
        return s.toString();
    }

    private static List<Cell> initGrid(int size) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells.add(new Cell(new Point2d(i, j)));
            }
        }
        return cells;
    }

    private static List<Cell> sort(List<Cell> cells) {
        cells.sort((c1, c2) -> {
            if (c1.position().x() == c2.position().x()) {
                return c1.position().y() - c2.position().y();
            }
            return c1.position().x() - c2.position().x();
        });
        return cells;
    }
}
