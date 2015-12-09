package com.exercise.messagebus.generic;

/**
 * An interface that represents a generic message bus
 *
 */
public interface GenericMessageBus {

    // Initialize the message bus with the host and port of the implementation
    public void initialize(String hostname, Integer port);

    // Create a channel publishing/consuming messages
    public GenericChannel createChannel();

    // Stop the message bus
    public void stop();

}
