package logic.grid;

import com.google.gson.*;
import logic.grid.cell.Cell;
import logic.user.UserImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridBuilder {

    public static final int GRID_SIZE = 9;
    public static final int SUBGRID_SIZE = 3;
    private static final int NUMBER_OF_EMPTY_BLOCK = 40; //40 is the perfect number

    public static Grid generatePartialSolution(){
        return generateGrid();
    }

    private static Grid generateGrid(){
        Grid solution = new GridImpl();
        fillGrid(solution);
        Grid newGrid = createPuzzle(solution);
        newGrid.getCells().forEach(cell -> {
            if(cell.getNumber().isPresent() && cell.getNumber().get() != 0){
                cell.setImmutable(true);
            }
        });
        return newGrid;
    }

    private static Grid createPuzzle(Grid grid){
        List<Cell> cells = new ArrayList<Cell>(grid.getCells());
        Random rand = new Random();
        int numberOfEmptyBlock = NUMBER_OF_EMPTY_BLOCK;

        while(numberOfEmptyBlock > 0){
            int row = rand.nextInt(GRID_SIZE);
            int col = rand.nextInt(GRID_SIZE);
            Cell cell = grid.getCellAt(row, col);
            if(cell.getNumber().isPresent()){
                cell.setAtEmpty();
                numberOfEmptyBlock--;
            }
        }

        grid.checkAndUpdateGrid(cells);
        return grid;
    }

    private static boolean fillGrid(Grid grid) {
        return solve(grid, 0, 0);
    }

    private static boolean solve(Grid grid, int row, int col) {
        if (row == GRID_SIZE) {
            return true;
        }

        int nextRow = col == GRID_SIZE - 1 ? row + 1 : row;
        int nextCol = (col + 1) % GRID_SIZE;

        Cell currentCell = grid.getCellAt(row, col);
        Random random = new Random();
        int[] numbers = random.ints(1, GRID_SIZE + 1).distinct().limit(GRID_SIZE).toArray();

        for (int num : numbers) {
            if (isValid(grid, row, col, num)) {
                currentCell.setNumber(num);
                if (solve(grid, nextRow, nextCol)) {
                    return true;
                }
                currentCell.setAtEmpty();
            }
        }
        return false;
    }

    private static boolean isValid(Grid grid, int row, int col, int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (grid.getCellAt(row, i).getNumber().orElse(0) == num ||
                    grid.getCellAt(i, col).getNumber().orElse(0) == num) {
                return false;
            }
        }

        int subgridRowStart = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridColStart = (col / SUBGRID_SIZE) * SUBGRID_SIZE;

        for (int i = subgridRowStart; i < subgridRowStart + SUBGRID_SIZE; i++) {
            for (int j = subgridColStart; j < subgridColStart + SUBGRID_SIZE; j++) {
                if (grid.getCellAt(i, j).getNumber().orElse(0) == num) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String toJson(Grid grid) {
        JsonArray jsonArray = new JsonArray();
        for (Cell cell : grid.getCells()) {
            JsonObject cellObject = new JsonObject();
            JsonObject positionObject = new JsonObject();
            positionObject.addProperty("x", cell.getPosition().x());
            positionObject.addProperty("y", cell.getPosition().y());
            cellObject.add("position", positionObject);
            cellObject.addProperty("isSelected", cell.isSelected().isPresent() ? cell.isSelected().get().name() : null);
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
                        c.setImmutable(true);
                    }
                    if (number != null){
                        c.setNumber(number);
                    }
                }
            }
        }
        return new GridImpl(grid.getCells());
    }

}