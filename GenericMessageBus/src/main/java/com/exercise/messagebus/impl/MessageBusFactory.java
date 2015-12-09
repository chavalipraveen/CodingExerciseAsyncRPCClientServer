/**
 * 
 */
package com.exercise.messagebus.impl;

import com.exercise.messagebus.generic.GenericMessageBus;
import com.exercise.messagebus.impl.rabbitmq.RMQMessageBus;

/**
 * A factory class to create and return a message bus instance based on the requested implementation key
 * Currently, only RABBIT_MQ_MESSAGE_BUS is implemented.
 *
 */
public class MessageBusFactory {

    public static final String RABBIT_MQ_MESSAGE_BUS = "RABBIT_MQ";
    public static final String UNIMPLEMENTED_MESSAGE_BUS = "UNIMPLEMENTED";

    public GenericMessageBus createMessageBus(String name) {
        if (RABBIT_MQ_MESSAGE_BUS.equals(name)) {
            return new RMQMessageBus();
        } else {
            return null;
        }
    }
}
