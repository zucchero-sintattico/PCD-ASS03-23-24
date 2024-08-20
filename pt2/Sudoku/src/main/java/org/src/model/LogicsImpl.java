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
    //private final GridBuilder gridBuilder = new GridBuilder();

    public LogicsImpl(Channel channel, String EXCHANGE_NAME) throws IOException {
        this.channel = channel;
        this.gridId = EXCHANGE_NAME;
        channel.queueDeclare(EXCHANGE_NAME, true, false, false, null);
    }

    public void sendMessageToServer(Grid grid) {

        if (channel != null && channel.isOpen()) {
            try {
                String message = grid.toJson();
                channel.basicPublish(gridId, "", null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Message sent to server: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Channel is closed. Cannot send message to server.");
        }
    }

    public void updateGridFromServer(Grid grid, String receivedMessage) {
        Grid newCellListFromServer = grid.formJson(receivedMessage);
        grid.updateGrid(newCellListFromServer.getCells());
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
        sendMessageToServer(grid);
        System.out.println("-----------------Sudoku Game-----------------");
        System.out.println(grid);
    }
}