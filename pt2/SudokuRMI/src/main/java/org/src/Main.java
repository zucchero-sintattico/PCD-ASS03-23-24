package org.src;


import org.src.common.Point2d;
import org.src.model.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) throws IOException, NotBoundException {
//        Cell c = new CellImpl(new Point2d(0, 0));
//        c.setNumber(5);
//        List<Cell> l1 = new ArrayList<>();
//        l1.add(c);
//        List<Cell> l2 = new ArrayList<>(l1);
//        System.out.println(l1);
//        System.out.println(l2);
//        c.setAtEmpty();
//        System.out.println(l1);
//        System.out.println(l2);
//        Grid g = new Grid(1);
//        g.cells().add(new Cell(new Point2d(0, 0), false, Optional.of(5), null));
//        System.out.println(g.cells());
//        Point2d p = new Point2d(0, 0);
//        Point2d p2 = new Point2d(0, 0);
//        System.out.println(p.equals(p2));
        SudokuFactory.createGrid();

//        String gridID = "1";
//        Grid grid = new RemoteGridImpl(new SudokuFactory().createGrid());
//        Grid gridStub = (Grid) UnicastRemoteObject.exportObject(grid, 0);
//        Registry registry = LocateRegistry.getRegistry();
//        registry.rebind(gridID, gridStub);
//
//        Grid remoteGrid = (Grid) registry.lookup(gridID);
//        System.out.println(remoteGrid.print());
//        System.out.println(remoteGrid.getCellAt(0, 0));



//        System.out.println("Enter your username: ");
//        String username = scanner.nextLine();
//
//        System.out.println("startNewGrid, true or false: ");
//        boolean startNewGrid = Boolean.parseBoolean(scanner.nextLine());
//
//        System.out.println("Enter grid Id: ");
//        String gridId = scanner.nextLine();
//
//        User user = new UserImpl(username);

//        LogicsImpl logics = new LogicsImpl(channel, gridId);
//        GridBuilder gridBuilder = new GridBuilder();
//        Grid grid = gridBuilder.generatePartialSolution();
//
//        AtomicReference<GridView> gridView = new AtomicReference<>();
//        AtomicBoolean viewIsSet = new AtomicBoolean(false);
//
//        if(!startNewGrid){
//            channel.basicPublish(gridId, MessageTopic.NEW_USER_JOINED.getTopic(), null, user.getName().getBytes());
//        }else{
//            gridView.set(new GridView(logics, user, grid));
//            viewIsSet.set(true);
//        }
//
//
//
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            if(delivery.getEnvelope().getRoutingKey().equals(MessageTopic.NEW_USER_JOINED.getTopic())){
//                if(viewIsSet.get()){
//                    logics.push(grid);
//                }
//            }
//
//            if (delivery.getEnvelope().getRoutingKey().equals(MessageTopic.UPDATE_GRID.getTopic())){
//                logics.pull(grid, new String(delivery.getBody()));
//
//                if(!viewIsSet.get()){
//                    gridView.set(new GridView(logics, user, grid));
//                    viewIsSet.set(true);
//                }
//            }
//            if(viewIsSet.get()){
//                gridView.get().updateGridView();
//            }
//            System.out.println(new String(delivery.getBody()));
//        };
//
//        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});



    }
}
