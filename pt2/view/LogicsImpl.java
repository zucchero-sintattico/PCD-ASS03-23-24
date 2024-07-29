package pt2.view;

import java.util.Random;
import java.util.stream.IntStream;

public class LogicsImpl implements Logics{

    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private int[][] grid;

    public LogicsImpl(){
        this.grid = new int[GRID_SIZE][GRID_SIZE];
        this.generateSudoku();
    }

    public int[][] generateSudoku() {
        fillGrid();
        return grid;
    }

    private void fillGrid() {
        fillDiagonal();
        fillRemaining(0, SUBGRID_SIZE);
    }

    private void fillDiagonal() {
        IntStream.iterate(0, i -> i + SUBGRID_SIZE).limit(GRID_SIZE / SUBGRID_SIZE).forEach(this::fillBox);
    }

    private boolean fillRemaining(int i, int j) {
        if (j >= GRID_SIZE && i < GRID_SIZE - 1) {
            i++;
            j = 0;
        }
        if (i >= GRID_SIZE && j >= GRID_SIZE) {
            return true;
        }
        if (i < SUBGRID_SIZE) {
            if (j < SUBGRID_SIZE) {
                j = SUBGRID_SIZE;
            }
        } else if (i < GRID_SIZE - SUBGRID_SIZE) {
            if (j == (i / SUBGRID_SIZE) * SUBGRID_SIZE) {
                j += SUBGRID_SIZE;
            }
        } else {
            if (j == GRID_SIZE - SUBGRID_SIZE) {
                i++;
                j = 0;
                if (i >= GRID_SIZE) {
                    return true;
                }
            }
        }

        int finalI = i;
        int finalJ = j;
        int finalI1 = i;
        int finalJ1 = j;
        return IntStream.rangeClosed(1, GRID_SIZE)
                .boxed()
                .filter(num -> isSafe(finalI, finalJ, num))
                .anyMatch(num -> {
                    grid[finalI1][finalJ1] = num;
                    if (fillRemaining(finalI1, finalJ1 + 1)) {
                        return true;
                    }
                    grid[finalI1][finalJ1] = 0;
                    return false;
                });
    }

    private void fillBox(int start) {
        Random random = new Random();
        IntStream.range(0, SUBGRID_SIZE).forEach(i ->
                IntStream.range(0, SUBGRID_SIZE).forEach(j -> {
                    int num;
                    do {
                        num = random.nextInt(GRID_SIZE) + 1;
                    } while (!isSafeInBox(start, start, num));
                    grid[start + i][start + j] = num;
                })
        );
    }

    private boolean isSafe(int i, int j, int num) {
        return isSafeInRow(i, num) && isSafeInCol(j, num) && isSafeInBox(i - i % SUBGRID_SIZE, j - j % SUBGRID_SIZE, num);
    }

    private boolean isSafeInRow(int i, int num) {
        return IntStream.range(0, GRID_SIZE).noneMatch(j -> grid[i][j] == num);
    }

    private boolean isSafeInCol(int j, int num) {
        return IntStream.range(0, GRID_SIZE).noneMatch(i -> grid[i][j] == num);
    }

    private boolean isSafeInBox(int row, int col, int num) {
        return IntStream.range(0, SUBGRID_SIZE)
                .noneMatch(i -> IntStream.range(0, SUBGRID_SIZE).anyMatch(j -> grid[row + i][col + j] == num));
    }

    public static void main(String[] args) {
        LogicsImpl generator = new LogicsImpl();
        int[][] sudoku = generator.generateSudoku();
        printGrid(sudoku);
    }

    private static void printGrid(int[][] grid) {
        IntStream.range(0, GRID_SIZE).forEach(i -> {
            IntStream.range(0, GRID_SIZE).forEach(j -> System.out.print(grid[i][j] + " "));
            System.out.println();
        });
    }

    @Override
    public int getNumber(int row, int col) {
        return this.grid[row][col];
    }
}
