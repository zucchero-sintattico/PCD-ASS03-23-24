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
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class ControllerImpl implements Controller{

    private Channel channel;
    private String gridId;
    private String queueName;
    private User user;
    private final Connection connection;
    private Grid grid;
    private SudokuView sudokuView;

    public ControllerImpl() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
    }

    public void push(Grid grid) throws IOException {
        this.channel.basicPublish(gridId, MessageTopic.UPDATE_GRID.getTopic(), null, GridImpl.toJson(grid).getBytes());
    }

    public Grid pull(String receivedMessage) {
        return GridImpl.formJson(receivedMessage);
    }

    private void createChannel() throws IOException {
        this.channel = this.connection.createChannel();
        this.channel.exchangeDeclare(this.gridId, "topic");
        this.queueName = this.channel.queueDeclare().getQueue();
        this.channel.queueBind(this.queueName, gridId, MessageTopic.UPDATE_GRID.getTopic());
        this.channel.queueBind(this.queueName, gridId, MessageTopic.USER_LEFT.getTopic());
    }

    private void setupChannel() throws IOException {
        this.createChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(delivery.getEnvelope().getRoutingKey().equals(MessageTopic.NEW_USER_JOINED.getTopic())){
                this.push(this.grid);
            }

            if (delivery.getEnvelope().getRoutingKey().equals(MessageTopic.UPDATE_GRID.getTopic())){
                if(this.grid == null){
                    this.channel.queueBind(this.queueName, gridId, MessageTopic.NEW_USER_JOINED.getTopic());
                    this.grid = new GridImpl();
                }
                this.grid.checkAndUpdateGrid(this.pull(new String(delivery.getBody())).getCells());
                this.sudokuView.update(this.grid);
            }
            System.out.println(new String(delivery.getBody()));
        };
        this.channel.basicConsume(this.queueName, true, deliverCallback, consumerTag -> {});
    }

    @Override
    public void createSudoku(String username, String sudokuId) throws IOException {
        this.gridId = sudokuId;
        Grid grid = new GridBuilder().generatePartialSolution();
        this.setupChannel();
        this.channel.basicPublish(gridId, MessageTopic.UPDATE_GRID.getTopic(), null, GridImpl.toJson(grid).getBytes());
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
    public String getGridId() {
        return this.gridId;
    }

    @Override
    public void selectCell(int x, int y) throws IOException {
        List<Cell> newCellList = new ArrayList<>();
        Grid newGrid = new GridImpl(this.grid.getCells()); //todo unsafe access to this.grid
        for (Cell cell : this.grid.getCells()) {
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
        //TODO non serve sto doppio controllo, che se tanto lo fai qua non mandi mai una griglia sbagliata, però pare pure brutto ave sta griglia che nse sa se è giusta o sbagliata? che ce famo? predict: sti cazzi niente (Forse a sto punto è meglio che se fidamo e teniamo na griglia che se la costruisci male ti lancia eccezione così che non la puoi pushare?)
        newGrid.checkAndUpdateGrid(newCellList);
        push(newGrid);
    }

    @Override
    public void makeMove(int number) throws IOException {
        List<Cell> newCellList = new ArrayList<>();
        Grid newGrid = new GridImpl();
        for (Cell cell : this.grid.getCells()) {
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
//        newGrid.checkAndUpdateGrid(newCellList);
        newGrid.updateGrid(newCellList);
        push(newGrid);
    }

    @Override
    public void setView(SudokuView view) {
        this.sudokuView = view;
    }

    @Override
    public void leave() throws IOException, TimeoutException {
        this.push(new GridImpl(this.deselectCell()));
        this.channel.close();
        this.grid = null;
    }

    private List<Cell> deselectCell() {
        return this.grid.getCells()
                .stream()
                .map(c -> {
                    if(c.isSelected().isPresent() && c.isSelected().get().getName().equals(user.getName())){
                       Cell cell = new CellImpl(c.getPosition(), c.isImmutable());
                       if (c.getNumber().isPresent()) {
                           cell.setNumber(c.getNumber().get());
                       }
                       return cell;
                    }
                    return c;
                }).toList();
    }

}
