package com.exercise.messagebus.impl.rabbitmq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;

import com.exercise.messagebus.generic.GenericChannel;
import com.exercise.messagebus.generic.GenericMessage;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * An implementation of the generic channel interface using RabbitMQ
 *
 */
public class RMQChannel implements GenericChannel {

    private final Logger log = LoggerFactory.getLogger(RMQChannel.class);

    Channel channel = null;
    String name = "";

    public RMQChannel(Connection connection) {
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            log.error("Error creating RabbitMQ channel: " + e.getLocalizedMessage());
        }
    }

    /**
     * Start a channel with a well-defined name
     */
    public void start(String name, Integer type) {
        this.name = name;
        try {
            if (type == GenericChannel.CHANNEL_SERVER) {
                channel.queueDeclare(name, false, false, false, null);
                channel.basicQos(1);
            } else if (type == GenericChannel.CHANNEL_CLIENT) {

            }
        } catch (IOException e) {
            log.error("Error starting RabbitMQ channel: " + e.getLocalizedMessage());
        }
    }

    /**
     * Start an anonymous channel (generally for clients)
     */
    public String startAnonymous(Integer type) {
        try {
            return channel.queueDeclare().getQueue();
        } catch (IOException e) {
            log.error("Error starting anonymous RabbitMQ channel: " + e.getLocalizedMessage());
        }
        return "";
    }

    /**
     * Publish a message on the queue name using a correlationId
     */
    public void publish(String queueName, String replyTo, String correlationId, String body) {
        BasicProperties replyProps;
        if ("".equals(queueName)) {
            replyProps = new BasicProperties.Builder().correlationId(correlationId).build();
        } else {
            replyProps = new BasicProperties.Builder().correlationId(correlationId).replyTo(queueName).build();
        }
        try {
            channel.basicPublish("", replyTo, replyProps, body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("Error publishing to RabbitMQ channel: " + e.getLocalizedMessage());
        } catch (IOException e) {
            log.error("Error publishing to RabbitMQ channel: " + e.getLocalizedMessage());
        }

    }

    /**
     * Consume a message, send the consumed message to a "consumer" ActorRef for asynchronous processing
     */
    public void consume(ActorRef consumerActor) {
        consume(this.name, consumerActor, "");
    }

    /**
     * Consume a message which matches a correlationId (a response to an earlier request)
     * Pass the message to a "consumer" ActorRef for asynchronous processing
     */
    public void consume(String queueName, ActorRef consumerActor, String corrId) {
        QueueingConsumer consumer = new QueueingConsumer(channel);
        try {
            channel.basicConsume(queueName, false, consumer);
            while (true) {

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                GenericMessage message = new GenericMessage();

                message.setBody(new String(delivery.getBody(), "UTF-8"));
                message.setCorrelationId(delivery.getProperties().getCorrelationId());
                message.setDeliveryTag(delivery.getEnvelope().getDeliveryTag());
                message.setReplyTo(delivery.getProperties().getReplyTo());

                consumerActor.tell(message, null);
                if (!"".equals(corrId) && corrId.equals(message.getCorrelationId())) {
                    log.info("Exiting consume loop");
                    break;
                }
            }

        } catch (IOException e) {
            log.error("Error consuming on RabbitMQ channel: " + e.getLocalizedMessage());
        } catch (ShutdownSignalException e) {
            log.error("Error consuming on RabbitMQ channel: " + e.getLocalizedMessage());
        } catch (ConsumerCancelledException e) {
            log.error("Error consuming on RabbitMQ channel: " + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            log.error("Error consuming on RabbitMQ channel: " + e.getLocalizedMessage());
        }

    }

    /**
     * Acknowledge a message receipt
     */
    public void ack(long deliveryTag) {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("Error consuming on RabbitMQ channel: " + e.getLocalizedMessage());
        }

    }

}
