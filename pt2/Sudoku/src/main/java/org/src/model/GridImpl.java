package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.Point2d;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class GridImpl implements Grid {

    List<Cell> cells = new ArrayList<>();
    private boolean isNew = true;
    int GRID_SIZE = 9;
    int SUBGRID_SIZE = 3;

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
        AtomicBoolean isAValidGrid = new AtomicBoolean(true);
        //Update grid if the move is valid
        cells.forEach(cell -> {
            if(!cell.isImmutable()){
                int x = cell.getPosition().x();
                int y = cell.getPosition().y();
                int number = cell.getNumber().orElse(0);

                if(number != 0){
                    if(this.isValidCell(cells, x, y)){
                        System.out.println("(" + x + ", " + y + ") is valid");
                    }else{
                        System.out.println("(" + x + ", " + y + ") is not valid");
                        isAValidGrid.set(false);
                    }
                }
            }
        });

        if(isAValidGrid.get()){
            this.cells = cells;
            System.out.println("Valid move");
        }else {
            System.out.println("Invalid Move");
        }
    }

    private boolean isValidCell(List<Cell> cells, int x, int y) {
        int number = getCellAt(x,y).getNumber().orElse(0);

        //Check if the number is unique in row, column and in the subgrid 3x3
        return isUniqueInRow(cells, x, y, number); //&&
                //isUniqueInColumn(cells, x, y, number) &&
                //isUniqueInSubgrid(cells, x, y, number);
    }

    private boolean isUniqueInRow(List<Cell> cells, int x, int y, int number) {
        return cells.stream()
                .filter(cell -> cell.getPosition().x() == x && cell.getPosition().y() != y)
                .noneMatch(cell -> cell.getNumber().orElse(0) == number);
    }

    private boolean isUniqueInColumn(List<Cell> cells, int x, int y, int number) {
        return cells.stream()
                .filter(cell -> cell.getPosition().y() == y && cell.getPosition().x() != x)
                .noneMatch(cell -> cell.getNumber().orElse(0) == number);
    }

    private boolean isUniqueInSubgrid(List<Cell> cells, int x, int y, int number) {
        int subgridX = (x / 3) * 3;
        int subgridY = (y / 3) * 3;

        return cells.stream()
                .filter(cell -> {
                    int cellX = cell.getPosition().x();
                    int cellY = cell.getPosition().y();
                    return cellX != x && cellY != y &&
                            cellX >= subgridX && cellX < subgridX + 3 &&
                            cellY >= subgridY && cellY < subgridY + 3;
                })
                .noneMatch(cell -> cell.getNumber().orElse(0) == number);
    }

    private boolean checkWin() {
        //Check if all cells are not empty with correct number
        return cells.stream()
                .allMatch(cell -> cell.getNumber().isPresent() && isValidCell(cells, cell.getPosition().x(), cell.getPosition().y()));
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
            cellObject.addProperty("isImmutable", cell.isImmutable() ? true : null);
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
            String immutable = null;
            if (cellObject.has("isImmutable")) {
                immutable = cellObject.get("isImmutable").isJsonNull() ? null : cellObject.get("isImmutable").getAsString();
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
                    if (immutable != null){
                        c.isImmutable(true);
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
        return cells.stream().noneMatch(e -> e.getNumber().isPresent());
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
