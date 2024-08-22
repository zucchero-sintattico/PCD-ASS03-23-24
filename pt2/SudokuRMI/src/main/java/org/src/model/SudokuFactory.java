package org.src.model;

import org.src.common.Point2d;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SudokuFactory {

    private static final Random random = new Random();
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int NUMBER_TO_REMOVE = 40;

    public static Grid createGrid() {
        return createPuzzle(getValidSudoku());
    }

    private static Grid createPuzzle(int[][] grid){
        int numberToRemove = NUMBER_TO_REMOVE;
        while(numberToRemove > 0){
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if(grid[row][col] != 0){
                grid[row][col] = 0;
                numberToRemove--;
            }
        }
        return convertToGrid(grid);
    }

    private static int[][] getValidSudoku(){
        int[][] grid = new int[SIZE][SIZE];
        fillGrid(grid,0, 0);
        return grid;
    }

    private static Grid convertToGrid(int[][] grid) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Point2d position = new Point2d(i, j);
                int number = grid[i][j];
                Cell cell;
                if(number != 0){
                    cell = new Cell(position, true).setNumber(number);
                }else{
                    cell = new Cell(position);
                }
                cells.add(cell);
            }
        }
        System.out.println(new Grid(cells).print());
        return new Grid(cells);
    }

    private static boolean fillGrid(int[][] grid, int row, int col) {
        if (row == SIZE) return true;

        int nextRow = col == SIZE - 1 ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        List<Integer> numbers = random.ints(1, SIZE + 1)
                .distinct().limit(SIZE).boxed().toList();

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
        boolean condition1 = !isAlreadyPresentInRowOrColumn(grid, row, col, num);
        boolean condition2 = !isAlreadyPresentInSubGrid(grid, row, col, num);
        return condition1 && condition2;
    }

    private static boolean isAlreadyPresentInRowOrColumn(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
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

}
