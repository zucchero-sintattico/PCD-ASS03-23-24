package pt2.view;

public interface Logics {
    boolean hit(int row, int col, int number);
    boolean won();
    int getNumber(int row, int col);
    void resetGame(int emptyCells);
}

