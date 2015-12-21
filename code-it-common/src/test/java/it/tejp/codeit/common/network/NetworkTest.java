package it.tejp.codeit.common.network;

import org.junit.Assert;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

/**
 * Created by tejp on 20/12/15.
 */
public class NetworkTest {

    private String str;

    @Before
    public void setUp() throws Exception {
        Random r = new Random();
        StringBuilder strb = new StringBuilder();
        for (int i = 0 ; i < 100000 ; i++) {
            char a = (char) r.nextInt(256);
            strb.append(a);
        }
        str = strb.toString();
    }

    @Test
    public void testChunkMessage() throws Exception {
        byte[] bytes = str.getBytes();
        List<MessageWithObject> list = Network.chunkMessage(bytes, 1013);


        byte[] newBytes = null;
        for (MessageWithObject m : list) {
            byte[] b = (byte[]) m.object;
            newBytes = ArrayUtils.addAll(newBytes, b);
        }

        String newStr = new String(newBytes);

        Assert.assertEquals(str, newStr);
    }
}
