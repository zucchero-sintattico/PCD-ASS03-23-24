package common.grid;

import common.Point2d;
import common.grid.cell.Cell;
import common.grid.cell.CellImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SudokuFactory {

    private static final Random random = new Random();
    public static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int NUMBER_TO_REMOVE = 40;

    public static SudokuGrid createGrid(List<Cell> cells) throws IllegalArgumentException {
        return new SudokuGridImpl(cells);
    }

    public static SudokuGrid createGrid() {
        return createPuzzle(getValidSudoku());
    }

    private static SudokuGrid createPuzzle(List<Cell> cells){
        AtomicInteger numberToRemove = new AtomicInteger(NUMBER_TO_REMOVE);
        while(numberToRemove.get() > 0){
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            Point2d position = new Point2d(row, col);
            cells = cells.stream().map(cell -> {
                if(cell.position().equals(position)){
                    numberToRemove.getAndDecrement();
                    return cell.removeNumber().setImmutable(false);
                }
                return cell;
            }).toList();
        }
        return createGrid(cells);
    }

    private static List<Cell> getValidSudoku(){
        List<Cell> cells = new ArrayList<>();
        fillGrid(cells,0, 0);
        return cells;
    }

    private static boolean fillGrid(List<Cell> cells, int row, int col) {
        if (row == GRID_SIZE) return true;

        int nextRow = col == GRID_SIZE - 1 ? row + 1 : row;
        int nextCol = (col + 1) % GRID_SIZE;

        List<Integer> numbers = random.ints(1, GRID_SIZE + 1)
                .distinct().limit(GRID_SIZE).boxed().toList();

        for (int n : numbers) {
            if (isValid(cells, row, col, n)) {
                Cell c = new CellImpl(new Point2d(row, col), true).setNumber(n);
                cells.add(c);
                if (fillGrid(cells, nextRow, nextCol)){
                    return true;
                }
                cells.remove(c);
            }
        }
        return false;
    }

    private static boolean isValid(List<Cell> cells, int row, int col, int num) {
        boolean condition1 = num >= 1 && num <= 9;
        boolean condition2 = !isAlreadyPresentInRowOrColumn(cells, row, col, num);
        boolean condition3 = !isAlreadyPresentInSubGrid(cells, row, col, num);
        return condition1 && condition2 && condition3;
    }

    private static boolean isAlreadyPresentInRowOrColumn(List<Cell> cells, int row, int col, int num) {
        List<Cell> tempCells = removeCellAtPosition(cells, new Point2d(row, col));
        List<Cell> cellWithSameNumberInRow = filterCellMatchingNum(tempCells, (cell -> cell.position().x() == row), num);
        List<Cell> cellWithSameNumberInCol = filterCellMatchingNum(tempCells, (cell -> cell.position().y() == col), num);
        return !cellWithSameNumberInRow.isEmpty() || !cellWithSameNumberInCol.isEmpty();
    }

    private static List<Cell> removeCellAtPosition(List<Cell> cells, Point2d position) {
        return cells.stream().filter(cell -> !cell.position().equals(position)).toList();
    }

    private static List<Cell> filterCellMatchingNum(List<Cell> cells, Predicate<Cell> predicate, int num){
        return cells.stream().filter(predicate).filter(cell -> cell.number().equals(Optional.of(num))).toList();
    }

    private static boolean isAlreadyPresentInSubGrid(List<Cell> cells, int row, int col, int num) {
        int subgridRowStart = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridColStart = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridRowEnd = subgridRowStart + SUBGRID_SIZE;
        int subgridColEnd = subgridColStart + SUBGRID_SIZE;
        List<Cell> tempCells = removeCellAtPosition(cells, new Point2d(row, col));
        List<Cell> subgrid = filterCellMatchingNum(tempCells, cell -> validPosition(cell.position(), subgridRowStart, subgridColStart, subgridRowEnd,subgridColEnd), num);
        return !subgrid.isEmpty();
    }

    public static boolean validateSudoku(List<Cell> grid) {
        if (!validateGridStructure(grid)) return false;
        List<Cell> testList = new ArrayList<>();
        for(Cell cell: grid) {
            if (cell.number().isPresent() && !isValid(testList, cell.position().x(), cell.position().y(), cell.number().get())) {
                return false;
            }else{
                testList.add(cell);
            }
        }
        return true;
    }

    private static boolean validateGridStructure(List<Cell> grid) {
        int sudokuSize = GRID_SIZE * GRID_SIZE;
        List<Cell> distinctCellByValidPosition = grid.stream()
                .collect(Collectors.toMap(Cell::position, c -> c))
                .values()
                .stream()
                .filter(cell -> validPosition(cell.position())).toList();
        return distinctCellByValidPosition.size() == sudokuSize;
    }

    private static boolean validPosition(Point2d position) {
        return validPosition(position, 0, 0, GRID_SIZE, GRID_SIZE);
    }

    private static boolean validPosition(Point2d position, int minX, int minY, int maxX, int maxY) {
        return position.x() >= minX &&
                position.x() < maxX &&
                position.y() >= minY &&
                position.y() < maxY;
    }

}