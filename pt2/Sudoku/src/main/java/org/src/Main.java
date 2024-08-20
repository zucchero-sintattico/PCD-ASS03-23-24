package org.src;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(Main.class);

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

        // Binding keys for new grid and grid updates
        String newGridKey = "grid.new";
        String updateGridKey = "grid.update";
        String userJoinKey = "grid.user.join";

        // Bind the queue to the exchange with the appropriate binding keys
        channel.queueBind(queueName, gridId, newGridKey);
        channel.queueBind(queueName, gridId, updateGridKey);

        Grid grid;
        if (startNewGrid) {
            GridBuilder gridBuilder = new GridBuilder();
            grid = gridBuilder.generatePartialSolution();
        } else {
            grid = new GridImpl();
            channel.basicPublish(gridId, userJoinKey, null, user.getName().getBytes());
        }

        LogicsImpl logics = new LogicsImpl(channel, gridId);
        GridView gridView = new GridView( logics,  user, grid);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            if (delivery.getEnvelope().getRoutingKey().equals(userJoinKey)) {
                System.out.println("User joined: " + new String(delivery.getBody(), StandardCharsets.UTF_8));
                logics.sendMessageToServer(grid);
            }

            if (delivery.getEnvelope().getRoutingKey().equals(updateGridKey)) {
                logics.updateGridFromServer(grid, new String(delivery.getBody(), StandardCharsets.UTF_8));
            }

            gridView.updateGridView();

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});


    }
}
