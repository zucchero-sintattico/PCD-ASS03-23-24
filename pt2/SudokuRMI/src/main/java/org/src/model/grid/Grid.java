package org.src.model.grid;

import org.src.common.Point2d;
import org.src.model.grid.cell.Cell;

import java.io.Serializable;
import java.util.List;

public interface Grid extends Serializable {

    List<Cell> cells();

}
