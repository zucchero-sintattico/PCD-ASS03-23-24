package org.src.controller;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.src.common.Grid;
import org.src.common.User;
import org.src.model.LogicsImpl;
import org.src.model.GridBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.src.model.MessageTopic;
import org.src.model.UserImpl;
import org.src.view.GridView;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ViewController {

    private static final Random random = new Random();
    private String gridId;
    private String queueName;
    private final User user;
    private Channel channel;
    private final Connection connection;
    private LogicsImpl logics;
    private Grid grid;
    private GridController gridController;
    private final AtomicReference<GridView> gridView = new AtomicReference<>();
    private final AtomicBoolean viewIsSet = new AtomicBoolean(false);

    public ViewController(String userId) throws IOException, TimeoutException {
        this.user = new UserImpl(userId);
        System.out.println("User in controller: " + this.user.getName());
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
    }

    public void setGridListener(GridController gridController){
        this.gridController = gridController;
    }

    private void createChannel() throws IOException {
        this.channel = this.connection.createChannel();
        this.channel.exchangeDeclare(this.gridId, "topic");
        this.queueName = this.channel.queueDeclare().getQueue();
        this.channel.queueBind(this.queueName, gridId, MessageTopic.NEW_USER_JOINED.getTopic());
        this.channel.queueBind(this.queueName, gridId, MessageTopic.UPDATE_GRID.getTopic());
        this.channel.queueBind(this.queueName, gridId, MessageTopic.USER_LEFT.getTopic());
    }

    public void startNewGame() throws IOException {
        //Generate grid id
        this.gridId = this.generateNewGridId();
        this.setupChannel();
        if(this.gridController != null){
            this.gridController.onGridReady(this.logics, this.user, this.gridId, this.grid);
        }
        this.viewIsSet.set(true);
    }

    //TODO: to change
    private String generateNewGridId() {
        //TODO: not generate the same GridID
        int number = 10000 + random.nextInt(90000);
        System.out.println("Session Id: " + number);
        return String.valueOf(number);
    }

    public void joinInGrid(String gridId) throws IOException {
        this.gridId = gridId;
        this.setupChannel();
        this.channel.basicPublish(gridId, MessageTopic.NEW_USER_JOINED.getTopic(), null, user.getName().getBytes());
    }

    //TODO: bug fix
    private void setupChannel() throws IOException {
        this.createChannel();
        this.logics = new LogicsImpl(this.channel, this.gridId);
        GridBuilder gridBuilder = new GridBuilder();
        this.grid = gridBuilder.generatePartialSolution();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(delivery.getEnvelope().getRoutingKey().equals(MessageTopic.NEW_USER_JOINED.getTopic())){
                if(this.viewIsSet.get()){
                    this.logics.push(this.grid);
                }
            }

            if (delivery.getEnvelope().getRoutingKey().equals(MessageTopic.UPDATE_GRID.getTopic())){
                this.logics.pull(this.grid, new String(delivery.getBody()));

                if(!this.viewIsSet.get()){
                    this.gridController.onGridReady(this.logics, this.user, this.gridId, this.grid);
                    this.viewIsSet.set(true);
                }
            }

            if(this.viewIsSet.get()){
                this.gridController.updateGrid(this.grid);
            }
            System.out.println(new String(delivery.getBody()));

            /*
            String routingKey = delivery.getEnvelope().getRoutingKey();
            String message = new String(delivery.getBody());

            if (routingKey.equals(MessageTopic.NEW_USER_JOINED.getTopic())) {
                this.logics.push(this.grid);
            }

            if (routingKey.equals(MessageTopic.UPDATE_GRID.getTopic())) {
                this.logics.pull(this.grid, message);
                this.gridController.updateGrid(this.grid);
            }

            System.out.println(message);

             */
        };
        this.channel.basicConsume(this.queueName, true, deliverCallback, consumerTag -> {});
    }
}
