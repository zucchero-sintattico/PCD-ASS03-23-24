package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.Point2d;

import java.util.ArrayList;
import java.util.List;


public class GridImpl implements Grid {

    List<Cell> cells = new ArrayList<>();

    int GRID_SIZE = 9;

    public GridImpl() {
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

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        cells.forEach(e -> {
            if (e.getNumber().isPresent()) {
                s.append(e.getNumber().get());
            } else {
                s.append("-");
            }
            s.append("   ");
            if (e.getPosition().y() == GRID_SIZE - 1) {
                s.append("\n");
            }

        });

        return s.toString();
    }

    @Override
    public String toJson() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        cells.forEach(e -> {
            s.append("\"").append(e.getPosition().x()).append(e.getPosition().y()).append("\":");
            if(e.getNumber().isPresent()){
                s.append(e.getNumber().get());
            }else{
                s.append("null");
            }
            s.append(",");
        });
        s.append("}");
        return s.toString();
    }

    @Override
    public Grid formJson(String json) {
        Grid grid = new GridImpl();
        json = json.replace("{", "").replace("}", ""); // remove the curly braces
        String[] cellData = json.split(",");
        for(String cell : cellData) {
            String[] data = cell.split(":");
            String pos = data[0].replace("\"", ""); // remove the quotes
            int x = Integer.parseInt(pos.substring(0, 1));
            int y = Integer.parseInt(pos.substring(1, 2));
            Integer number = data[1].equals("null") ? null : Integer.parseInt(data[1]);
            List<Cell> newCellList= new ArrayList<>();
            for (Cell c : grid.getCells()) {
                if(c.getPosition().x() == x && c.getPosition().y() == y){
                    if (number != null){
                        c.setNumber(number);
                    }
                }
                newCellList.add(c);
            }
            grid.updateGrid(newCellList);
        }
        return grid;
    }

    @Override
    public boolean isEmpty() {
        return cells.stream().noneMatch(e -> e.getNumber().isPresent());
    }
}

