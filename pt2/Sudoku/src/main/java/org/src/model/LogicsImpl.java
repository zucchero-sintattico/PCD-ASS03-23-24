package org.src.model;

import com.rabbitmq.client.Channel;
import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LogicsImpl {

    private final Channel channel;
    private final String gridId;
    private static final String UPDATE_GRID_KEY = "grid.update";
    private static final String USER_MOVE_KEY = "grid.move";

    public LogicsImpl(Channel channel, String gridId) throws IOException {
        this.channel = channel;
        this.gridId = gridId;
    }

    public void sendMessageToServer(Grid grid) {
        if (channel != null && channel.isOpen()) {
            try {
                if (grid.isEmpty()) {
                    System.err.println("Empty grid. Cannot send message to server.");
                    return;
                }
                String message = grid.toJson();
                channel.basicPublish(gridId, UPDATE_GRID_KEY, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Message sent to server: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Channel is closed or empty grid. Cannot send message to server.");
        }
    }

    public void updateGridFromServer(Grid grid, String receivedMessage) {
        Grid newGridFromServer = grid.formJson(receivedMessage);
        grid.updateGrid(newGridFromServer.getCells());
        System.out.println("-----------------Sudoku Game-----------------");
        System.out.println(grid);
    }

    public void selectCell(Grid grid, User user, int x, int y) {
        List<Cell> newCellList = new ArrayList<>();
        for (Cell cell : grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition());
            if (cell.getNumber().isPresent()) {
                newCell.setNumber(cell.getNumber().get());
            }

            if (cell.isSelected().isPresent() && !cell.isSelected().get().getName().equals(user.getName())) {
                newCell.selectCell(cell.isSelected().get());
            }

            if (cell.getPosition().x() == x && cell.getPosition().y() == y) {
                newCell.selectCell(user);
            }
            newCellList.add(newCell);
        }
        grid.updateGrid(newCellList);
        sendMessageToServer(grid);
        System.out.println("-----------------Sudoku Game-----------------");
        System.out.println(grid);
    }

    public void makeMove(Grid grid, User user, int number) throws IOException {
        List<Cell> newCellList = new ArrayList<>();

        for (Cell cell : grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition());
            if (cell.getNumber().isPresent()) {
                newCell.setNumber(cell.getNumber().get());
            }
            if (cell.isSelected().isPresent() && cell.isSelected().get().getName().equals(user.getName())) {
                newCell.setNumber(number);
            }
            if (cell.isSelected().isPresent() && !cell.isSelected().get().getName().equals(user.getName())) {
                newCell.selectCell(cell.isSelected().get());
            }
            newCellList.add(newCell);
        }
        grid.updateGrid(newCellList);
        sendMoveToServer(grid, user, number);
        System.out.println("-----------------Sudoku Game-----------------");
        System.out.println(grid);
    }

    private void sendMoveToServer(Grid grid, User user, int number) throws IOException {
        if (channel != null && channel.isOpen()) {
            String message = String.format("%s:%d:%s", user.getName(), number, grid.toJson());
            channel.basicPublish(gridId, USER_MOVE_KEY, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("User move sent to server: " + message);
        } else {
            System.err.println("Channel is closed. Cannot send move to server.");
        }
    }
}
