package it.tejp.codeit.common.network;

import com.esotericsoftware.kryo.Kryo;

/**
 * Created by tejp on 20/11/15.
 */
public class Initializer {
    static public void registerClasses(Kryo kryo) {
        Message.register(kryo);
        MessageWithObject.register(kryo);
    }
}
