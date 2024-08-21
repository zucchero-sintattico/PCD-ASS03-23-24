package org.src.model;

import com.rabbitmq.client.Channel;
import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LogicsImpl {

    private final Channel channel;
    private final String gridId;

    public LogicsImpl(Channel channel, String gridId) throws IOException {
        this.channel = channel;
        this.gridId = gridId;
    }

    public void selectCell(Grid grid, User user, int x, int y) throws IOException {

    }

    public void makeMove(Grid grid, User user, int number) throws IOException {}


//    public void selectCell(Grid grid, User user, int x, int y) throws IOException {
//        List<Cell> newCellList = new ArrayList<>();
//        for (Cell cell : grid.getCells()) {
//                    Cell newCell = new CellImpl(cell.getPosition(), cell.setImmutable());
//
//                    if (cell.getNumber().isPresent()) {
//                        newCell.setNumber(cell.getNumber().get());
//                    }
//
//                    if (cell.isSelected().isPresent() && !cell.isSelected().get().getName().equals(user.getName())) {
//                        newCell.select(cell.isSelected().get());
//                    }
//
//                    if (cell.getPosition().x() == x && cell.getPosition().y() == y && !cell.setImmutable()) {
//                        newCell.select(user);
//                        newCellList.add(newCell);
//                    }
//            newCellList.add(newCell);
//        }
//        grid.updateGrid(newCellList);
//        push(grid);
//    }
//
//    public void makeMove(Grid grid, User user, int number) throws IOException {
//        List<Cell> newCellList = new ArrayList<>();
//
//        for (Cell cell : grid.getCells()) {
//            Cell newCell = new CellImpl(cell.getPosition(), cell.setImmutable());
//            if (cell.getNumber().isPresent()) {
//                newCell.setNumber(cell.getNumber().get());
//            }
//            if (cell.isSelected().isPresent() && cell.isSelected().get().getName().equals(user.getName())) {
//                newCell.setNumber(number);
//            }
//            if (cell.isSelected().isPresent() && !cell.isSelected().get().getName().equals(user.getName())) {
//                newCell.select(cell.isSelected().get());
//            }
//            newCellList.add(newCell);
//        }
//        grid.checkAndUpdateGrid(newCellList);
//        push(grid);
//    }


}
