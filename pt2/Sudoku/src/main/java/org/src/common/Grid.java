package org.src.common;

import java.util.List;

public interface Grid {
    List<Cell> getCells();
    void updateGrid(List<Cell> cells);

    String toJson();

    Grid formJson(String json);

    Cell getCellAt(int row, int col);

    boolean isEmpty();
}
