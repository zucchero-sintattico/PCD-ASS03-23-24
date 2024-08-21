package org.src.model;

import org.src.common.Cell;
import org.src.common.Grid;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudokuFactory{

    private static final Random random = new Random();
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int NUMBER_OF_EMPTY_BLOCK = 40;

    public static Grid createGrid() throws RemoteException {
        Grid grid = new GridImpl();
        fillGrid(grid,0,0);
        System.out.println(grid.print());
        return grid;

//        return this.generateGrid();
    }

    /* Generate the complete solution of sudoku */
    private Grid generateGrid() throws RemoteException {
        Grid solution = new GridImpl();
//        if(this.fillGrid(solution)){
//            Grid newGrid = this.createPuzzle(solution, NUMBER_OF_EMPTY_BLOCK);
//            newGrid.getCells().forEach(cell -> {
//                if(cell.getNumber().isPresent() && cell.getNumber().get() != 0){
//                    cell.setImmutable(true);
//                }
//            });
//            return newGrid;
//        }else{
//            throw new RuntimeException("It's not possible to generate a grid");
//        }
        return solution;
    }

    /*Take the solution grid and remove cell to create puzzle*/
    private Grid createPuzzle(Grid grid, int numberOfEmptyBlock) throws RemoteException {
        List<Cell> cells = new ArrayList<Cell>(grid.getCells());
        Random rand = new Random();

        while(numberOfEmptyBlock > 0){
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            Cell cell = grid.getCellAt(row, col);
            if(cell.getNumber().isPresent()){
                cell.setAtEmpty();
                numberOfEmptyBlock--;
            }
        }
        grid.updateGrid(cells);
        return grid;
    }

//    private static Grid fillGrid(Grid grid) throws RemoteException {
//        return solve(grid, 0, 0);
//    }

    private static boolean fillGrid(Grid grid, int row, int col) throws RemoteException {
        if (row == SIZE) { return true; }

        int nextRow = col == SIZE - 1 ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        Cell currentCell = grid.getCellAt(row, col);
        List<Integer> numbers = random.ints(1, SIZE + 1)
                .distinct().limit(SIZE).boxed().toList();

        for (int n : numbers) {
            if (isValid(grid, row, col, n)) {
                currentCell.setNumber(n);
                if (fillGrid(grid, nextRow, nextCol)) {
                    return true;
                }
                currentCell.setAtEmpty();
            }
        }
        return false;
    }

    private static boolean isValid(Grid grid, int row, int col, int num) throws RemoteException {
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

    /* Only for debug */
    public void printGrid(Grid grid) throws RemoteException {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = grid.getCellAt(row, col);
                int number = cell.getNumber().orElse(0);
                System.out.print(number == 0 ? "." : number);
                if (col < SIZE - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

}
