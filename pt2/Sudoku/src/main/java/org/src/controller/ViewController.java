package org.src.controller;

import com.rabbitmq.client.ConnectionFactory;
import org.src.common.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.src.model.UserImpl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ViewController {

    private Channel channel;
    private Connection connection;
    private ConnectionFactory connectionFactory;
    private User user;

    public ViewController(String username) throws IOException, TimeoutException {
        this.user = new UserImpl(username);
        this.connectionFactory = new ConnectionFactory();
        this.connectionFactory.setHost("localhost");
        this.connection = this.connectionFactory.newConnection();
        this.channel = this.connection.createChannel();
    }
}
