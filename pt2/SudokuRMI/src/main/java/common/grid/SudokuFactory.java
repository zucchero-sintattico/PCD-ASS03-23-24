package common.grid;

import common.Point2d;
import common.grid.cell.Cell;
import common.grid.cell.CellImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private static SudokuGrid createPuzzle(int[][] grid){
        int numberToRemove = NUMBER_TO_REMOVE;
        while(numberToRemove > 0){
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            if(grid[row][col] != 0){
                grid[row][col] = 0;
                numberToRemove--;
            }
        }
        return convertToGrid(grid);
    }

    private static SudokuGrid convertToGrid(int[][] grid) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Point2d position = new Point2d(i, j);
                int number = grid[i][j];
                Cell cell;
                if(number != 0){
                    cell = new CellImpl(position, true).setNumber(number);
                }else{
                    cell = new CellImpl(position);
                }
                cells.add(cell);
            }
        }
        return SudokuFactory.createGrid(cells);
    }

    private static int[][] getValidSudoku(){
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        fillGrid(grid,0, 0);
        return grid;
    }

    private static boolean fillGrid(int[][] grid, int row, int col) {
        if (row == GRID_SIZE) return true;

        int nextRow = col == GRID_SIZE - 1 ? row + 1 : row;
        int nextCol = (col + 1) % GRID_SIZE;

        List<Integer> numbers = random.ints(1, GRID_SIZE + 1)
                .distinct().limit(GRID_SIZE).boxed().toList();

        for (int n : numbers) {
            if (isValid(grid, row, col, n)) {
                grid[row][col] = n;
                if (fillGrid(grid, nextRow, nextCol)){
                    return true;
                }
                grid[row][col] = 0;
            }
        }
        return false;
    }

    private static boolean isValid(int[][] grid, int row, int col, int num) {
        boolean condition1 = num >= 1 && num <= 9;
        boolean condition2 = !isAlreadyPresentInRowOrColumn(grid, row, col, num);
        boolean condition3 = !isAlreadyPresentInSubGrid(grid, row, col, num);
        return condition1 && condition2 && condition3;
    }

    private static boolean isAlreadyPresentInRowOrColumn(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAlreadyPresentInSubGrid(int[][] grid, int row, int col, int num) {
        int subgridRowStart = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridColStart = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        for (int i = subgridRowStart; i < subgridRowStart + SUBGRID_SIZE; i++) {
            for (int j = subgridColStart; j < subgridColStart + SUBGRID_SIZE; j++) {
                if (grid[i][j] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean validateSudoku(List<Cell> grid) {
        try {
            int[][] matrix = convertToMatrix(grid);
            int[][] testMatrix = new int[GRID_SIZE][GRID_SIZE];
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (matrix[i][j] != 0 && !isValid(testMatrix, i, j, matrix[i][j])) {
                        return false;
                    } else {
                        testMatrix[i][j] = matrix[i][j];
                    }
                }
            }
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    private static int[][] convertToMatrix(List<Cell> grid) throws IllegalArgumentException {
        if (!validateGridStructure(grid)) throw new IllegalArgumentException("Invalid input");
        int[][] matrix = new int[GRID_SIZE][GRID_SIZE];
        for (Cell cell : grid) {
            Point2d position = cell.position();
            Integer number = cell.number().orElse(0);
            matrix[position.x()][position.y()] = number;
        }
        return matrix;
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
        return position.x() >= 0 &&
                position.x() < GRID_SIZE &&
                position.y() >= 0 &&
                position.y() < GRID_SIZE;
    }

}