package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.Point2d;
import com.google.gson.*;

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
        cells.forEach(e -> {
            if (e.isSelected().isPresent()) {
                s.append(e.isSelected().get().getName()).append(" -> ");
                s.append(e.getPosition().x());
                s.append(",");
                s.append(e.getPosition().y());
                s.append("\n");
            }
        });


        return s.toString();
    }

    @Override
    public String toJson() {
        JsonArray jsonArray = new JsonArray();
        for (Cell cell : cells) {
            JsonObject cellObject = new JsonObject();
            JsonObject positionObject = new JsonObject();
            positionObject.addProperty("x", cell.getPosition().x());
            positionObject.addProperty("y", cell.getPosition().y());
            cellObject.add("position", positionObject);
            cellObject.addProperty("isSelected", cell.isSelected().isPresent() ? cell.isSelected().get().getName() : null);
            cellObject.addProperty("number", cell.getNumber().isPresent() ? cell.getNumber().get() : null);
            jsonArray.add(cellObject);
        }
        return new Gson().toJson(jsonArray);
    }

    @Override
    public Grid formJson(String json) {
        Grid grid = new GridImpl();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject cellObject = jsonElement.getAsJsonObject();
            JsonObject positionObject = cellObject.get("position").getAsJsonObject();
            int x = positionObject.get("x").getAsInt();
            int y = positionObject.get("y").getAsInt();
            String selected = null;
            if (cellObject.has("isSelected")) {
                selected = cellObject.get("isSelected").isJsonNull() ? null : cellObject.get("isSelected").getAsString();
            }
            Integer number = null;
            if (cellObject.has("number")) {
                number = cellObject.get("number").isJsonNull() ? null : cellObject.get("number").getAsInt();
            }
            List<Cell> newCellList= new ArrayList<>();
            for (Cell c : grid.getCells()) {
                if(c.getPosition().x() == x && c.getPosition().y() == y){
                    if (selected != null){
                        c.selectCell(new UserImpl(selected));
                    }
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
    public Cell getCellAt(int row, int col) {
        return this.getCells()
                .stream()
                .filter(cell -> cell.getPosition().x() == row && cell.getPosition().y() == col)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cell not found at position (" + row + ", " + col + ")"));
    }

    @Override
    public boolean isEmpty() {
        return cells.stream().noneMatch(e -> e.getNumber().isPresent()) && cells.stream().noneMatch(e -> e.isSelected().isPresent());
    }
}
