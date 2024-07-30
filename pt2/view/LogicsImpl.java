package pt2.view;


import java.util.Random;

public class LogicsImpl implements Logics {
    private int[][] solution; // La soluzione completa del Sudoku
    private int[][] partialSolution; // La griglia parzialmente riempita mostrata all'utente
    private int[][] board; // La griglia attuale del Sudoku su cui l'utente gioca
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    public LogicsImpl(int emptyCells) {
        this.solution = new int[SIZE][SIZE];
        this.partialSolution = new int[SIZE][SIZE];
        this.board = new int[SIZE][SIZE];
        this.genSolution();
        this.genPartialSolution(emptyCells);
    }

    @Override
    public boolean hit(int row, int col, int number) {
        if (this.solution[row][col] == number) {
            this.board[row][col] = number;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean won() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] != this.solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getNumber(int row, int col) {
        return this.partialSolution[row][col];
    }

    @Override
    public void resetGame(int emptyCells) {
        this.solution = new int[SIZE][SIZE];
        this.partialSolution = new int[SIZE][SIZE];
        this.board = new int[SIZE][SIZE];
        this.genSolution();
        this.genPartialSolution(emptyCells);
    }

    private void genSolution() {
        this.fillGrid(this.solution);
    }

    private boolean fillGrid(int[][] grid) {
        Random random = new Random();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    int[] numbers = this.randomizeNumbers();
                    for (int number : numbers) {
                        if (this.isValid(grid, row, col, number)) {
                            grid[row][col] = number;
                            if (this.fillGrid(grid)) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private int[] randomizeNumbers() {
        Random random = new Random();
        int[] numbers = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            numbers[i] = i + 1;
        }
        for (int i = 0; i < SIZE; i++) {
            int randomIndex = random.nextInt(SIZE);
            int temp = numbers[i];
            numbers[i] = numbers[randomIndex];
            numbers[randomIndex] = temp;
        }
        return numbers;
    }

    private boolean isValid(int[][] grid, int row, int col, int number) {
        return !this.isInRow(grid, row, number) &&
                !this.isInCol(grid, col, number) &&
                !this.isInBox(grid, row, col, number);
    }

    private boolean isInRow(int[][] grid, int row, int number) {
        for (int col = 0; col < SIZE; col++) {
            if (grid[row][col] == number) {
                return true;
            }
        }
        return false;
    }

    private boolean isInCol(int[][] grid, int col, int number) {
        for (int row = 0; row < SIZE; row++) {
            if (grid[row][col] == number) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBox(int[][] grid, int row, int col, int number) {
        int boxRowStart = row - row % SUBGRID_SIZE;
        int boxColStart = col - col % SUBGRID_SIZE;
        for (int r = 0; r < SUBGRID_SIZE; r++) {
            for (int c = 0; c < SUBGRID_SIZE; c++) {
                if (grid[boxRowStart + r][boxColStart + c] == number) {
                    return true;
                }
            }
        }
        return false;
    }

    private void genPartialSolution(int emptyCells) {
        // Copia la soluzione completa nella griglia parziale
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                this.partialSolution[row][col] = this.solution[row][col];
            }
        }

        // Rimuove casualmente il numero specificato di celle
        Random random = new Random();
        while (emptyCells > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if (this.partialSolution[row][col] != 0) {
                this.partialSolution[row][col] = 0;
                emptyCells--;
            }
        }

        // Inizializza la board con la partialSolution
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                this.board[row][col] = this.partialSolution[row][col];
            }
        }
    }

    public void printGrid(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int emptyCells = 40; // Numero di celle vuote da generare
        LogicsImpl game = new LogicsImpl(emptyCells);

        System.out.println("Generated Sudoku Solution:");
        game.printGrid(game.solution);

        System.out.println("\nInitial Partial Solution (what user sees):");
        game.printGrid(game.partialSolution);

        // Example of how to play the game
        System.out.println("\nHit (0, 0, 5): " + game.hit(0, 0, 5));
        System.out.println("Won: " + game.won());

        // Example of getNumber
        System.out.println("\nNumber at (0, 0): " + game.getNumber(0, 0));

        // Resetting the game
        game.resetGame(emptyCells);
        System.out.println("\nNew Generated Sudoku Solution:");
        game.printGrid(game.solution);

        System.out.println("\nNew Initial Partial Solution (what user sees):");
        game.printGrid(game.partialSolution);
    }
}
