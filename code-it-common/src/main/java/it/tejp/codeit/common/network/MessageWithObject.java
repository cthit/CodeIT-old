package it.tejp.codeit.common.network;

import com.esotericsoftware.kryo.Kryo;

import java.io.Serializable;

/**
 * Created by kerp on 20/11/15.
 */
public class MessageWithObject implements Serializable{

    public Message message;
    public Object object;

    public MessageWithObject(Message message, Object object) {
        this.message = message;
        this.object = object;
    }

    public MessageWithObject () {
    }

    public static void register(Kryo k) {
        k.register(MessageWithObject.class);
        k.register(byte[].class);
    }
}
