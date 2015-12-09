/**
 * 
 */
package com.exercise.messagebus.impl.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exercise.messagebus.generic.GenericChannel;
import com.exercise.messagebus.generic.GenericMessageBus;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * An implementation of a generic message bus using RabbitMQ
 *
 */
public class RMQMessageBus implements GenericMessageBus {

    private final Logger log = LoggerFactory.getLogger(RMQMessageBus.class);

    String hostname;
    String port;
    Connection connection = null;

    /**
     * Initialize the message bus with the host and port of the implementation
     */
    public void initialize(String hostname, Integer port) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setPort(port);

        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            log.error("Error initializing RabbitMQ message bus: " + e.getLocalizedMessage());
        } catch (TimeoutException e) {
            log.error("Error initializing RabbitMQ message bus: " + e.getLocalizedMessage());
        }
    }

    /**
     * Create a channel publishing/consuming messages
     */
    public GenericChannel createChannel() {
        return new RMQChannel(connection);
    }

    /**
     * Stop the message bus
     */
    public void stop() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                log.error("Error closing RabbitMQ message bus: " + e.getLocalizedMessage());
            }
        }

    }

}
