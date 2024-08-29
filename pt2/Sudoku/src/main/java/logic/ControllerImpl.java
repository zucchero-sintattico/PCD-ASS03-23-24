package logic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import logic.grid.Grid;
import logic.grid.GridBuilder;
import logic.grid.GridImpl;
import logic.grid.cell.Cell;
import logic.grid.cell.CellImpl;
import logic.user.User;
import view.SudokuView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ControllerImpl implements Controller{

    private Channel channel;
    private String gridId;
    private String queueName;
    private User user;
    private final Connection connection;
    private volatile Grid grid;
    private SudokuView sudokuView;

    public ControllerImpl() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
    }

    private void createChannel() throws IOException {
        this.channel = this.connection.createChannel();
        this.channel.exchangeDeclare(this.gridId, "topic");
        this.queueName = this.channel.queueDeclare().getQueue();
        this.channel.queueBind(this.queueName, this.gridId, MessageTopic.UPDATE_GRID.getTopic());
    }

    private void setupChannel() throws IOException {
        this.createChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(delivery.getEnvelope().getRoutingKey().equals(MessageTopic.NEW_USER_JOINED.getTopic())){
                this.push(this.grid);
            }
            if (delivery.getEnvelope().getRoutingKey().equals(MessageTopic.UPDATE_GRID.getTopic())){
                this.pull(new String(delivery.getBody()));
            }
        };
        this.channel.basicConsume(this.queueName, true, deliverCallback, consumerTag -> {});
    }

    private void push(Grid grid) throws IOException {
        this.channel.basicPublish(this.gridId, MessageTopic.UPDATE_GRID.getTopic(), null, GridBuilder.toJson(grid).getBytes());
    }

    private void pull(String receivedMessage) throws IOException {
        try {
            Grid newGrid = GridBuilder.formJson(receivedMessage);
            if(this.grid == null){
                this.channel.queueBind(this.queueName, this.gridId, MessageTopic.NEW_USER_JOINED.getTopic());
            }
            this.grid = newGrid;
            this.sudokuView.update(this.grid);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createSudoku(String username, String sudokuId) throws IOException {
        this.gridId = sudokuId;
        this.setupChannel();
        Grid grid = GridBuilder.generatePartialSolution();
        this.channel.basicPublish(this.gridId, MessageTopic.UPDATE_GRID.getTopic(), null, GridBuilder.toJson(grid).getBytes());
    }

    @Override
    public void joinSudoku(String username, String sudokuId) throws IOException {
        this.gridId = sudokuId;
        this.setupChannel();
        this.channel.basicPublish(this.gridId, MessageTopic.NEW_USER_JOINED.getTopic(), null, this.user.name().getBytes());
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
    public void selectCell(int x, int y) throws IOException, IllegalArgumentException, NullPointerException {
        List<Cell> newCellList = new ArrayList<>();
        Grid newGrid = new GridImpl();
        for (Cell cell : this.grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition(), cell.isImmutable());

            if (cell.getNumber().isPresent()) {
                newCell.setNumber(cell.getNumber().get());
            }
            //todo modify using user equality
            if (cell.isSelected().isPresent() && !cell.isSelected().get().name().equals(this.user.name())) {
                newCell.selectCell(cell.isSelected().get());
            }

            if (cell.getPosition().x() == x && cell.getPosition().y() == y && !cell.isImmutable()) {
                newCell.selectCell(this.user);
                newCellList.add(newCell);
            }
            newCellList.add(newCell);
        }
        newGrid.checkAndUpdateGrid(newCellList);
        push(newGrid);
    }

    @Override
    public void makeMove(int number) throws IOException, IllegalArgumentException, NullPointerException {
        List<Cell> newCellList = new ArrayList<>();
        Grid newGrid = new GridImpl();
        for (Cell cell : this.grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition(), cell.isImmutable());
            if (cell.getNumber().isPresent()) {
                newCell.setNumber(cell.getNumber().get());
            }
            if (cell.isSelected().isPresent() && cell.isSelected().get().name().equals(this.user.name())) {
                newCell.setNumber(number);
            }
            if (cell.isSelected().isPresent() && !cell.isSelected().get().name().equals(this.user.name())) {
                newCell.selectCell(cell.isSelected().get());
            }
            newCellList.add(newCell);
        }
        newGrid.checkAndUpdateGrid(newCellList);
        push(newGrid);
    }

    @Override
    public void setView(SudokuView view) {
        this.sudokuView = view;
    }

    @Override
    public void leave() throws IOException, TimeoutException, NullPointerException {
        this.push(new GridImpl(this.deselectCell()));
        this.channel.close();
        this.grid = null;
    }

    private List<Cell> deselectCell() throws NullPointerException{
        return this.grid.getCells()
                .stream()
                .map(c -> {
                    if(c.isSelected().isPresent() && c.isSelected().get().name().equals(this.user.name())){
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