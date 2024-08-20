package org.src;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.src.common.Grid;
import org.src.common.User;
import org.src.model.GridBuilder;
import org.src.model.GridImpl;
import org.src.model.LogicsImpl;
import org.src.model.UserImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import org.src.view.GridView;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter grid Id: ");
        String gridId = scanner.nextLine();


        User user = new UserImpl(username);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(gridId, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, gridId, "");

        GridBuilder gridBuilder = new GridBuilder();
        Grid grid = gridBuilder.generatePartialSolution();
        LogicsImpl logics = new LogicsImpl(channel, gridId);
        GridView view = new GridView(logics, user, grid);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + receivedMessage + "'");

                if (receivedMessage.equals("new_grid") && grid.isNew()) {
                    logics.sendMessageToServer(grid);
                    grid.setNew(false);
                    System.out.println("New User Join Started!");
                } else if (!receivedMessage.equals("new_grid")) {
                    logics.updateGridFromServer(grid, receivedMessage);
                    System.out.println("Grid Sent!");
                }
                view.updateGridView();

            } catch (Exception e) {
                System.err.println("Error handling delivery: " + e.getMessage());
                e.printStackTrace();
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

//        if (!grid.isEmpty()) {
//            logics.sendMessageToServer(grid);
//        } else {
        String message = "new_grid";
        channel.basicPublish(gridId, "", null, message.getBytes());
        System.out.println("New grid Started!");
        System.out.println(grid);
//        }

        System.out.println("-----------------Sudoku Game-----------------");
        System.out.println(grid);



    }
}