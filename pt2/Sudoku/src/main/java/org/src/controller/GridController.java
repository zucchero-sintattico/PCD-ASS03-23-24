package org.src.controller;

import org.src.common.Grid;
import org.src.common.User;
import org.src.model.LogicsImpl;

public interface GridController {
    void onGridReady(LogicsImpl logics, User user, String gridId, Grid grid);
    void updateGrid(Grid grid);
}
