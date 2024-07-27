package org.src;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.src.common.Cell;
import org.src.common.Grid;
import org.src.model.GridImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {
    private static final String EXCHANGE_NAME = "logs";
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");


        //-----Make Grid------/
        Grid grid = new GridImpl();

        //-----receive the grid from the server------/
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + receivedMessage + "'");

            if (receivedMessage.equals("new_grid") && !grid.isEmpty()){
                //send the grid to the server
                String message = grid.toJson();
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            }else if (!receivedMessage.equals("new_grid")){
                Grid newCellListFromServer = grid.formJson(receivedMessage);
                grid.updateGrid(newCellListFromServer.getCells());
                System.out.println("-----------------Sudoku Game-----------------");
                System.out.println(grid);
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });



        while (true) {

            //If new connection is made, receive the grid from the server


            //-----send the grid to the server------/
            if (!grid.isEmpty()){
                String message = grid.toJson();
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            }else {
                //ask for a new grid
                String message = "new_grid";
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());

                System.out.println("New grid Started!");
                System.out.println(grid);

            }

            //-----make a move------/
            System.out.println("Enter a position: formatted as x y");
            String position = scanner.nextLine();
            String[] split = position.split(" ");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            System.out.println("Enter a number: ");
            int number = Integer.parseInt(scanner.nextLine());
            List<Cell> newCellList= new ArrayList<>();
            for (Cell cell : grid.getCells()) {
                if(cell.getPosition().x() == x && cell.getPosition().y() == y){
                    cell.setNumber(number);
                }
                newCellList.add(cell);
            }

        }


    }

}