/**
 * 
 */
package com.exercise.messagebus.generic;

import akka.actor.ActorRef;

/**
 * A generic channel interface for a message bus
 *
 */
public interface GenericChannel {

    public static final Integer CHANNEL_CLIENT = 0; // For use with clients
    public static final Integer CHANNEL_SERVER = 1; // For use with servers

    // Start a channel with a well-defined name
    public void start(String name, Integer type);

    // Start an anonymous channel (generally for clients)
    public String startAnonymous(Integer type);

    // Publish a message on the queue name using a correlationId
    public void publish(String queueName, String replyTo, String correlationId, String body);

    // Consume a message, send the consumed message to a "consumer" ActorRef for asynchronous processing
    public void consume(ActorRef consumer);

    // Consume a message which matches a correlationId (a response to an earlier request)
    // Pass the message to a "consumer" ActorRef for asynchronous processing
    public void consume(String queueName, ActorRef consumer, String correlationId);

    // Acknowledge a message receipt
    public void ack(long deliveryTag);
}
