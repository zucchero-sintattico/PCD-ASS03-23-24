package org.src.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.User;
import org.src.model.*;
import org.src.view.SudokuView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerImpl implements Controller{

    private Channel channel;
    private String gridId;
    private String queueName;
    private User user;
    private final Connection connection;
    private Grid grid;
    private SudokuView sudokuView;
    private final AtomicBoolean viewIsSet = new AtomicBoolean(false);

    public ControllerImpl() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
    }

    public void push(Grid grid) throws IOException {
        channel.basicPublish(gridId, MessageTopic.UPDATE_GRID.getTopic(), null, grid.toJson().getBytes());
    }

    public void pull(Grid grid, String receivedMessage) {
        Grid newGrid = grid.formJson(receivedMessage);
        grid.updateGrid(newGrid.getCells());
        System.out.println(grid);
    }

    private void createChannel() throws IOException {
        this.channel = this.connection.createChannel();
        this.channel.exchangeDeclare(this.gridId, "topic");
        this.queueName = this.channel.queueDeclare().getQueue();
        this.channel.queueBind(this.queueName, gridId, MessageTopic.NEW_USER_JOINED.getTopic());
        this.channel.queueBind(this.queueName, gridId, MessageTopic.UPDATE_GRID.getTopic());
        this.channel.queueBind(this.queueName, gridId, MessageTopic.USER_LEFT.getTopic());
    }

    private void setupChannel() throws IOException {
        this.createChannel();
        GridBuilder gridBuilder = new GridBuilder();
        this.grid = gridBuilder.generatePartialSolution();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(delivery.getEnvelope().getRoutingKey().equals(MessageTopic.NEW_USER_JOINED.getTopic())){
                if(this.viewIsSet.get()){
                    this.push(this.grid);
                }
            }

            if (delivery.getEnvelope().getRoutingKey().equals(MessageTopic.UPDATE_GRID.getTopic())){
                this.pull(this.grid, new String(delivery.getBody()));

                if(!this.viewIsSet.get()){
                    this.sudokuView.update(this.grid);
                    this.viewIsSet.set(true);
                }
            }

            if(this.viewIsSet.get()){
                this.sudokuView.update(this.grid);
            }
            System.out.println(new String(delivery.getBody()));
        };
        this.channel.basicConsume(this.queueName, true, deliverCallback, consumerTag -> {});
    }

    @Override
    public void createSudoku(String username) throws IOException {
        this.gridId = this.getGridId();
        this.setupChannel();
        if(this.sudokuView != null){
            this.sudokuView.update(this.grid);
        }
        this.viewIsSet.set(true);
    }

    @Override
    public void joinSudoku(String username, String sudokuId) throws IOException {
        this.gridId = sudokuId;
        this.setupChannel();
        this.channel.basicPublish(gridId, MessageTopic.NEW_USER_JOINED.getTopic(), null, user.getName().getBytes());
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    @Override
    public String getGridId() {
        return this.gridId;
    }

    @Override
    public void selectCell(Grid grid, User user, int x, int y) throws IOException {
        List<Cell> newCellList = new ArrayList<>();
        for (Cell cell : grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition(), cell.isImmutable());

            if (cell.getNumber().isPresent()) {
                newCell.setNumber(cell.getNumber().get());
            }

            if (cell.isSelected().isPresent() && !cell.isSelected().get().getName().equals(user.getName())) {
                newCell.selectCell(cell.isSelected().get());
            }

            if (cell.getPosition().x() == x && cell.getPosition().y() == y && !cell.isImmutable()) {
                newCell.selectCell(user);
                newCellList.add(newCell);
            }
            newCellList.add(newCell);
        }
        grid.updateGrid(newCellList);
        push(grid);
    }

    @Override
    public void makeMove(Grid grid, User user, int number) throws IOException {
        List<Cell> newCellList = new ArrayList<>();

        for (Cell cell : grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition(), cell.isImmutable());
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
        grid.checkAndUpdateGrid(newCellList);
        push(grid);
    }

    @Override
    public void setView(SudokuView view) {
        this.sudokuView = view;
    }

}
