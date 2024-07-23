package view;

public interface Grid {
    void addUser(User user);
    void removeUser(User user);
    int setGridSize(int gridSize);
    int getGridSize();
    int getValue(int row, int col);
    void setValue(int row, int col, int value);
    boolean isValidMove(int row, int col, int value);
    void selectCell(int row, int col, User user);
}
