package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.Point2d;
import com.google.gson.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class GridImpl implements Grid {

    private List<Cell> cells = new ArrayList<>();
    private final static int GRID_SIZE = 9;

    public GridImpl(List<Cell> cells){
        //todo check input?? could be nice, maybe
        this.cells = new ArrayList<>(cells);
    }
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

    public void checkAndUpdateGrid(List<Cell> cells) {
        AtomicBoolean isAValidGrid = new AtomicBoolean(true);
        //Update grid if the move is valid
        cells.forEach(cell -> {
            if(!cell.isImmutable() && cell.getNumber().isPresent()){
                int x = cell.getPosition().x();
                int y = cell.getPosition().y();
                    if(!this.isValidCell(cells, cell)){
                        System.out.println("(" + x + ", " + y + ") is not valid");
                        isAValidGrid.set(false);
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

    @Override
    public boolean haveWon() {
        return cells.stream().allMatch(cell -> cell.getNumber().isPresent());
    }

    @Override
    public void updateGrid(List<Cell> cells) {
        this.cells = cells;
    }

    private boolean isValidCell(List<Cell> cells, Cell currentCell) {
        //Check if the number is unique in row, column and in the subgrid 3x3
        return isPositive(currentCell) &&
                isUniqueInRow(cells, currentCell) &&
                isUniqueInColumn(cells, currentCell) &&
                isUniqueInSubgrid(cells, currentCell);
    }

    private boolean isPositive(Cell currentCell){
        Optional<Integer> currentNumber = currentCell.getNumber();
        return currentNumber.isEmpty() || currentNumber.get() >= 0;
    }

    private boolean isUniqueInRow(List<Cell> cells, Cell currentCell) {
        int currentX = currentCell.getPosition().x();
        int currentY = currentCell.getPosition().y();
        Optional<Integer> currentNumber = currentCell.getNumber();

        return currentNumber.map(integer -> cells.stream()
                .filter(cell -> cell.getPosition().x() == currentX &&
                        cell.getPosition().y() != currentY)
                .map(Cell::getNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .noneMatch(x -> x.equals(integer))).orElse(true);

    }

    private boolean isUniqueInColumn(List<Cell> cells, Cell currentCell) {
        int currentX = currentCell.getPosition().x();
        int currentY = currentCell.getPosition().y();
        Optional<Integer> currentNumber = currentCell.getNumber();

        return currentNumber.map(integer -> cells.stream()
                .filter(cell -> cell.getPosition().x() != currentX &&
                        cell.getPosition().y() == currentY)
                .map(Cell::getNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .noneMatch(x -> x.equals(integer))).orElse(true);
    }

    private boolean isUniqueInSubgrid(List<Cell> cells, Cell currentCell) {
        int currentX = currentCell.getPosition().x();
        int currentY = currentCell.getPosition().y();
        int startRow = (currentX / 3) * 3;
        int startCol = (currentY / 3) * 3;
        Optional<Integer> currentNumber = currentCell.getNumber();

        return currentNumber.map(integer -> cells.stream()
                .filter(cell -> isInSameSubgrid(cell, startRow, startCol))
                .filter(cell -> !(cell.getPosition().x() == currentX && cell.getPosition().y() == currentY))
                .map(Cell::getNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .noneMatch(number -> number.equals(integer))).orElse(true);

    }

    private boolean isInSameSubgrid(Cell cell, int startRow, int startCol) {
        int x = cell.getPosition().x();
        int y = cell.getPosition().y();
        return x >= startRow && x < startRow + 3 && y >= startCol && y < startCol + 3;
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

    public static String toJson(Grid grid) {
        JsonArray jsonArray = new JsonArray();
        for (Cell cell : grid.getCells()) {
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

    public static Grid formJson(String json) {
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
            }
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

}
