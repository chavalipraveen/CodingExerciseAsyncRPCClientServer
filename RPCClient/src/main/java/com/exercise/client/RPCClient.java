package com.exercise.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.exercise.messagebus.generic.GenericChannel;
import com.exercise.messagebus.generic.GenericMessage;
import com.exercise.messagebus.generic.GenericMessageBus;
import com.exercise.messagebus.impl.MessageBusFactory;

/**
 * Main RPC Client class
 * It initializes the GenericMessageBus and GenericChannel
 * We use RabbitMQ as the message bus implementation in this exercise
 * (RABBIT_MQ_MESSAGE_BUS).
 * 
 * We create an Akka actor which would be responsible for receiving the reply from the server
 * This is because, this client itself may be running as part of any other service, so, we don't
 * want to block the thread waiting for a response from the RPC Server
 *
 */
public class RPCClient extends UntypedActor {

    LoggingAdapter actorLog = Logging.getLogger(getContext().system(), this);

    private static Logger log = LoggerFactory.getLogger(RPCClient.class);

    private static ActorSystem system = ActorSystem.create("RPCClient");

    private static GenericMessageBus bus = null;
    private static GenericChannel channel = null;

    private static ActorRef client = system.actorOf(Props.create(RPCClient.class));

    private static String requestQueueName = "rpc_queue";
    private static String replyQueueName;

    private static String corrId = "";

    public void call(String message) throws Exception {

    }

    public static void main(String[] argv) {

        /**
         * Create a message bus implementation for RabbitMQ
         */
        String mbHost, mbPort;
        InputStream stream = ClassLoader.getSystemResourceAsStream("project.properties");

        Properties props = new Properties();
        try {
            props.load(stream);
            mbHost = props.getProperty("message.bus.host", "localhost");
            mbPort = props.getProperty("message.bus.port", "5672");
            log.info("Using host (" + mbHost + ") and port (" + mbPort + ") for message bus");
        } catch (IOException e1) {
            log.error("Error reading project properties. Using defaults hostname: localhost and port: 5672");
            mbHost = "localhost";
            mbPort = "5672";
        }

        mbHost = props.getProperty("message.bus.host", "localhost");
        mbPort = props.getProperty("message.bus.port", "5672");

        int port = 5672;
        try {
            port = Integer.parseInt(mbPort);
        } catch (NumberFormatException e) {
            port = 5672;
        }

        MessageBusFactory factory = new MessageBusFactory();
        try {
            bus = factory.createMessageBus(MessageBusFactory.RABBIT_MQ_MESSAGE_BUS);

            bus.initialize(mbHost, port);

            channel = bus.createChannel();
        } catch (Exception e1) {
            log.error("Exiting because of exception: " + e1.getLocalizedMessage());
            System.exit(-1);
        }

        // Start an anonymous channel which will receive the server's responses
        replyQueueName = channel.startAnonymous(GenericChannel.CHANNEL_CLIENT);
        corrId = UUID.randomUUID().toString();

        if (argv.length > 0) {
            // Use the user input as of expression to be parsed
            client.tell(argv[0], null);
        } else {
            // Use a dummy expression as input
            String dummyExpression = "2+(9-2)*4/2;.";
            client.tell(dummyExpression, null);
        }

        // Consume loop
        channel.consume(replyQueueName, client, corrId);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.info(e.getLocalizedMessage());
        }
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof String) {
            /**
             * Send a message over the message bus to the server
             * The message body contains the expression to be parsed and evaluated
             */
            actorLog.info("Requesting parse of statement: " + (String) msg);
            channel.publish(replyQueueName, requestQueueName, corrId, (String) msg);
        } else if (msg instanceof GenericMessage) {
            /**
             * We got a response from the server, extract the result or error
             * When done, just exit.
             */
            GenericMessage message = (GenericMessage) msg;
            String response = message.getBody();
            actorLog.info("Got response: " + response);
            getContext().stop(getSelf());
            actorLog.info("Exiting...");
            if (bus != null) {
                try {
                    bus.stop();
                } catch (Exception e) {
                    actorLog.error("Exception in stopping message bus: " + e.getLocalizedMessage());
                }
            }
            System.exit(0);
        }

    }
}
