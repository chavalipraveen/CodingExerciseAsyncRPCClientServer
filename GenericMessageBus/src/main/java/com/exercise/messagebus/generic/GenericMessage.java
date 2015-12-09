/**
 * 
 */
package com.exercise.messagebus.generic;

/**
 * A class which represents a simple message format to be exchanged on a message bus
 *
 */
public class GenericMessage {

    private String replyTo = "";        // Reply queue name
    private long deliveryTag;           // Delivery tag of the original message (used for acks)
    private String correlationId = "";  // Correlation Id (for matching a request to a response)
    private String body;                // Content of the message

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setDeliveryTag(long l) {
        this.deliveryTag = l;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public long getDeliveryTag() {
        return deliveryTag;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getBody() {
        return body;
    }
}
