package com.exercise.server;

import java.io.BufferedReader;
import java.io.StringReader;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.exercise.messagebus.generic.GenericChannel;
import com.exercise.messagebus.generic.GenericMessage;
import com.exercise.parsers.rd.SimpleRDParser;
import com.exercise.parsers.rd.Tokenizer;

/**
 * Does the work of parsing the input expression tree and calculate result 
 *
 */
public class RPCWorker extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    GenericChannel channel = null;

    public RPCWorker(GenericChannel channel) {
        this.channel = channel;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        String response = "";
        if (msg instanceof GenericMessage) {
            /**
             * Extract the message body from the GenericMessage that the client sent us.
             * The message body contains the expression to be parsed and evaluated.
             */
            GenericMessage message = (GenericMessage) msg;
            String correlationId = message.getCorrelationId();
            String replyTo = message.getReplyTo();
            String body = message.getBody();
            long deliveryTag = message.getDeliveryTag();
            try {
                log.info("Parsing (" + body + ") using recursive descent");
                response = "" + rdParse(body);
            } catch (Exception e) {
                log.error(e.toString());
            } finally {
                channel.publish("", replyTo, correlationId, response);

                channel.ack(deliveryTag);

                getContext().stop(getSelf());
            }
        }

    }

    /**
     * Call the recursive descent parser on this input
     * @param body - input expression to parse
     * @return - result of expression evaluation as a String
     */
    private String rdParse(String body) {
        StringReader sr = new StringReader(body);
        BufferedReader br = new BufferedReader(sr);
        Tokenizer tokenizer = new Tokenizer(br);
        SimpleRDParser parser = new SimpleRDParser(tokenizer);
        return parser.parse();
    }
}
