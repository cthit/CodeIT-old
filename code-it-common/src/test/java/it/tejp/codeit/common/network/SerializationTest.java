package it.tejp.codeit.common.network;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tejp on 20/12/15.
 */
public class SerializationTest {

    @Test
    public void testSerialization() throws Exception {
        String string = "Random test string to test serialization";

        MessageWithObject m = new MessageWithObject(Message.CHUNK, string);

        byte[] bytes = Serializer.serialize(m);
        MessageWithObject deserializedM = (MessageWithObject) Serializer.deserialize(bytes);

        Message oldM = m.message;
        Message newM = deserializedM.message;

        String oldS = (String) m.object;
        String newS = (String) deserializedM.object;

        Assert.assertEquals(oldM, newM);
        Assert.assertEquals(oldS, newS);
    }
}
