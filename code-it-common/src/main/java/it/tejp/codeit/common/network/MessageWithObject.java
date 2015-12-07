package it.tejp.codeit.common.network;

/**
 * Created by kerp on 20/11/15.
 */
public class MessageWithObject {

    public Message message;
    public Object object;

    public MessageWithObject(Message message, Object object) {
        this.message = message;
        this.object = object;
    }
}
