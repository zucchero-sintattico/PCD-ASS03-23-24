package org.src;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.src.common.Cell;
import org.src.common.Grid;
import org.src.common.User;
import org.src.model.CellImpl;
import org.src.model.GridImpl;
import org.src.model.UserImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {
    private static final String EXCHANGE_NAME = "logs";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        User user = new UserImpl(username);


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

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
                    sendMessageToServer(grid, channel);
                }else if (!receivedMessage.equals("new_grid")){
                    updateGridFromServer(grid, receivedMessage);
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            while (true) {
                //-----send the grid to the server------/
                if (!grid.isEmpty()){
                    sendMessageToServer(grid, channel);
                }else {
                    //ask for a new grid
                    String message = "new_grid";
                    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());

                    System.out.println("New grid Started!");
                    System.out.println(grid);
                }

                //-----make a move------/
                makeMove(grid, user, channel);
            }
        }
    }

    private static void sendMessageToServer(Grid grid, Channel channel) throws IOException {
        String message = grid.toJson();
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
    }

    private static void updateGridFromServer(Grid grid, String receivedMessage) {
        Grid newCellListFromServer = grid.formJson(receivedMessage);
        grid.updateGrid(newCellListFromServer.getCells());
        System.out.println("-----------------Sudoku Game-----------------");
        System.out.println(grid);
    }


    private static void makeMove(Grid grid, User user,  Channel channel) throws IOException {
        List<Cell> newCellList= new ArrayList<>();
        System.out.println("Select a position: formatted as x y");
        String position = scanner.nextLine();
        String[] split = position.split(" ");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        for (Cell cell : grid.getCells()) {
            if(cell.getPosition().x() == x && cell.getPosition().y() == y){
                cell.selectCell(user);
            }
            newCellList.add(cell);
        }
        grid.updateGrid(newCellList);
        sendMessageToServer(grid, channel);

        newCellList= new ArrayList<>();
        System.out.println("Enter a number: ");
        int number = Integer.parseInt(scanner.nextLine());
        for (Cell cell : grid.getCells()) {
            Cell newCell = new CellImpl(cell.getPosition());
            if (cell.getNumber().isPresent()){
                newCell.setNumber(cell.getNumber().get())   ;
            }
            if(cell.getPosition().x() == x && cell.getPosition().y() == y){
                newCell.setNumber(number);
            }
            if (cell.isSelected().isPresent() && !cell.isSelected().get().getName().equals(user.getName())){
                newCell.selectCell(cell.isSelected().get());
            }
            newCellList.add(newCell);
        }
        grid.updateGrid(newCellList);
    }
}