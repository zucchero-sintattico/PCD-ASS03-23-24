package org.src;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.src.common.Grid;
import org.src.common.User;
import org.src.model.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.src.view.GridView;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("startNewGrid, true or false: ");
        boolean startNewGrid = Boolean.parseBoolean(scanner.nextLine());

        System.out.println("Enter grid Id: ");
        String gridId = scanner.nextLine();

        User user = new UserImpl(username);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare a topic exchange
        channel.exchangeDeclare(gridId, "topic");
        String queueName = channel.queueDeclare().getQueue();

        // Bind the queue to the exchange with the appropriate binding keys
        channel.queueBind(queueName, gridId, MessageTopic.NEW_USER_JOINED.getTopic());
        channel.queueBind(queueName, gridId, MessageTopic.UPDATE_GRID.getTopic());
        channel.queueBind(queueName, gridId, MessageTopic.USER_LEFT.getTopic());

        LogicsImpl logics = new LogicsImpl(channel, gridId);
        GridBuilder gridBuilder = new GridBuilder();
        Grid grid = gridBuilder.generatePartialSolution();

        AtomicReference<GridView> gridView = new AtomicReference<>();
        AtomicBoolean viewIsSet = new AtomicBoolean(false);

        if(!startNewGrid){
            channel.basicPublish(gridId, MessageTopic.NEW_USER_JOINED.getTopic(), null, user.getName().getBytes());
        }else{
            gridView.set(new GridView(logics, user, gridId, grid));
            viewIsSet.set(true);
        }



        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(delivery.getEnvelope().getRoutingKey().equals(MessageTopic.NEW_USER_JOINED.getTopic())){
                if(viewIsSet.get()){
                    logics.push(grid);
                }
            }

            if (delivery.getEnvelope().getRoutingKey().equals(MessageTopic.UPDATE_GRID.getTopic())){
                logics.pull(grid, new String(delivery.getBody()));

                if(!viewIsSet.get()){
                    gridView.set(new GridView(logics, user, gridId, grid));
                    viewIsSet.set(true);
                }
            }
            if(viewIsSet.get()){
                gridView.get().updateGridView();
            }
            System.out.println(new String(delivery.getBody()));
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
