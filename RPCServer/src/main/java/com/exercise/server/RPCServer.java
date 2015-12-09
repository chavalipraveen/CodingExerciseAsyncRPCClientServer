package com.exercise.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.exercise.messagebus.generic.GenericChannel;
import com.exercise.messagebus.generic.GenericMessageBus;
import com.exercise.messagebus.impl.MessageBusFactory;

/**
 * Main RPC Server class
 * It initializes the GenericMessageBus and GenericChannel
 * We use RabbitMQ as the message bus implementation in this exercise
 * (RABBIT_MQ_MESSAGE_BUS).
 * 
 * The processing of the message from the client is done asynchronously
 * (using Akka Actors and the "tell" method for sending message to child actors).
 *
 */
public class RPCServer extends UntypedActor {

    private static final Logger log = LoggerFactory.getLogger(RPCServer.class);

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static ActorSystem system = ActorSystem.create("RPCServer");

    private static GenericMessageBus bus = null;
    private static GenericChannel channel = null;

    private static ActorRef server = system.actorOf(Props.create(RPCServer.class));

    public static void main(String[] argv) {

        try {
            /**
             * Create a message bus implementation for RabbitMQ
             */
            MessageBusFactory factory = new MessageBusFactory();
            bus = factory.createMessageBus(MessageBusFactory.RABBIT_MQ_MESSAGE_BUS);
            bus.initialize("localhost", 5672);

            channel = bus.createChannel();

            // Start the channel with a well-defined queue name
            channel.start(RPC_QUEUE_NAME, GenericChannel.CHANNEL_SERVER);

            log.info("RPC Server listening for messages...");

            // Consume loop
            channel.consume(server);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        } finally {
            if (bus != null) {
                try {
                    bus.stop();
                } catch (Exception e) {
                    log.error("Exception in stopping message bus: " + e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        /**
         * We only expect one type of message as of now. So, there is no filtering for the messages
         * Just spawn a worker actor which will do the work for us.
         * Use tell syntax, so we don't have to wait.
         */
        ActorRef computeActor = system.actorOf(Props.create(RPCWorker.class, channel));
        computeActor.tell(message, null);
    }
}
