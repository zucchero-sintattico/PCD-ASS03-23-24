package org.src.common;

import java.util.List;

public interface Grid {
    List<Cell> getCells();
    void updateGrid(List<Cell> cells);

    String toJson();

    Grid formJson(String json);

    Cell getCellAt(int row, int col);

    boolean isEmpty();

    boolean isNew();
    void setNew(boolean isNew);

    void checkAndUpdateGrid(List<Cell> newCellList);

    boolean haveWon();
}
