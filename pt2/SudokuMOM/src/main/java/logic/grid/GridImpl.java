package logic.grid;

import common.Point2d;
import logic.grid.cell.Cell;
import logic.grid.cell.CellImpl;

import java.util.*;

public class GridImpl implements Grid {

    private List<Cell> cells = new ArrayList<>();
    
    public GridImpl(List<Cell> cells) throws IllegalArgumentException {
        this.checkAndUpdateGrid(cells);
    }

    public GridImpl() {
        for (int i = 0; i < GridBuilder.GRID_SIZE; i++) {
            for (int j = 0; j < GridBuilder.GRID_SIZE; j++) {
                this.cells.add(new CellImpl(new Point2d(i, j)));
            }
        }
    }

    @Override
    public List<Cell> getCells() {
        return cells;
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
    public void checkAndUpdateGrid(List<Cell> cells) throws IllegalArgumentException {
        for(Cell cell: cells){
            if(!cell.isImmutable() && cell.getNumber().isPresent()){
                if(!this.isValidCell(cells, cell)){
                    throw new IllegalArgumentException("Invalid grid");
                }
            }
        }
        this.cells = new ArrayList<>(cells);
    }

    @Override
    public boolean haveWon() {
        return cells.stream().allMatch(cell -> cell.getNumber().isPresent());
    }

    private boolean isValidCell(List<Cell> cells, Cell currentCell) {
        return isNumberValid(currentCell) &&
                isUniqueInRow(cells, currentCell) &&
                isUniqueInColumn(cells, currentCell) &&
                isUniqueInSubgrid(cells, currentCell);
    }

    private boolean isNumberValid(Cell currentCell){
        int number = currentCell.getNumber().orElse(1);
        return number > 0 && number <= GridBuilder.GRID_SIZE;
    }

    private boolean isUniqueInRow(List<Cell> cells, Cell currentCell) {
        int currentX = currentCell.getPosition().x();
        int currentY = currentCell.getPosition().y();
        Optional<Integer> currentNumber = currentCell.getNumber();

        return currentNumber.isEmpty() || cells.stream()
                .filter(cell -> cell.getPosition().x() == currentX && cell.getPosition().y() != currentY)
                .noneMatch(cell -> cell.getNumber().equals(currentNumber));

    }

    private boolean isUniqueInColumn(List<Cell> cells, Cell currentCell) {
        int currentX = currentCell.getPosition().x();
        int currentY = currentCell.getPosition().y();
        Optional<Integer> currentNumber = currentCell.getNumber();

        return currentNumber.isEmpty() || cells.stream()
                .filter(cell -> cell.getPosition().x() != currentX && cell.getPosition().y() == currentY)
                .noneMatch(cell -> cell.getNumber().equals(currentNumber));
    }

    private boolean isUniqueInSubgrid(List<Cell> cells, Cell currentCell) {
        int currentX = currentCell.getPosition().x();
        int currentY = currentCell.getPosition().y();
        int startRow = (currentX / 3) * 3;
        int startCol = (currentY / 3) * 3;
        Optional<Integer> currentNumber = currentCell.getNumber();

        return currentNumber.isEmpty() || cells.stream()
                .filter(cell -> isInSameSubgrid(cell, startRow, startCol))
                .filter(cell -> cell.getPosition().x() != currentX && cell.getPosition().y() != currentY)
                .noneMatch(cell -> cell.getNumber().equals(currentNumber));
    }

    private boolean isInSameSubgrid(Cell cell, int startRow, int startCol) {
        int x = cell.getPosition().x();
        int y = cell.getPosition().y();
        return x >= startRow && x < startRow + GridBuilder.SUBGRID_SIZE && y >= startCol && y < startCol + GridBuilder.SUBGRID_SIZE;
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
            if (e.getPosition().y() == GridBuilder.GRID_SIZE - 1) {
                s.append("\n");
            }

        });
        cells.forEach(e -> {
            if (e.isSelected().isPresent()) {
                s.append(e.isSelected().get().name()).append(" -> ");
                s.append(e.getPosition().x());
                s.append(",");
                s.append(e.getPosition().y());
                s.append("\n");
            }
        });

        return s.toString();
    }

}