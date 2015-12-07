package it.tejp.codeit.common.network;

import com.esotericsoftware.kryo.Kryo;

import java.io.File;

/**
 * Created by tejp on 20/11/15.
 */
public class Initializer {
    static public void registerClasses(Kryo kryo) {
        kryo.register(Message.class);
        kryo.register(SourceFile.class);
        kryo.register(File.class);
        kryo.register(MessageWithObject.class);
    }
}
