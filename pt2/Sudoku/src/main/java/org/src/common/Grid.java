package org.src.common;

import java.util.List;

public interface Grid {
    List<Cell> getCells();
    void updateGrid(List<Cell> cells);
}
