package it.chalmers.digit.codeit.common.network;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryonet.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions for communication over the network.
 */
public class Network {

    public static final int BUFFER_SIZE = 16384;
    private static final int SEND_DELAY = 50;

    /**
     * Sends the given MessageWithObject to a connection, makes sure that the message can be sent regardless of size.
     * @param connection The connection to send the message on.
     * @param msg The message that is to be sent.
     * @throws IOException Throws an exception if the message can't be serialized.
     */
    public static void sendMessageWithObject(Connection connection, MessageWithObject msg) throws IOException {
        byte[] content = Serializer.serialize(msg);
        int contentSize = content.length;

        List<MessageWithObject> chunks = chunkMessage(content, BUFFER_SIZE);

        MessageWithObject chunkedTransfer = new MessageWithObject(Message.CHUNKED_TRANSFER, contentSize);
        connection.sendTCP(chunkedTransfer);

        // Implement better logging here
        // System.out.println("Sending " + chunks.size() + " messages");

        try {
            for (MessageWithObject chunk : chunks) {
                connection.sendTCP(chunk);
                try {
                    Thread.sleep(SEND_DELAY);
                } catch (InterruptedException e) {
                    System.out.println("Network.sendMessageWithObject: InterruptedException");
                    e.printStackTrace();
                    connection.sendTCP(Message.TRANSFER_ERROR);
                }
            }
        } catch (KryoException e) {
            // Implement better logging here
            System.out.println("Network.sendMessageWithObject: KryoException");
            e.printStackTrace();
            connection.sendTCP(Message.TRANSFER_ERROR);
        }

    }

    /**
     * Takes a byte array and splits it up in chunks that are guaranteed to be less than or equal to chunkSize in size.
     * @param bytes The byte array to split up
     * @param chunkSize The maximum size of the array parts.
     * @return
     */
    protected static List<MessageWithObject> chunkMessage(byte[] bytes, int chunkSize) {
        chunkSize = (int) (chunkSize*0.9);
        int numberOfChunks = (int)Math.ceil(bytes.length / (chunkSize));
        List<MessageWithObject> chunks = new ArrayList<>();

        // Implement better logging here
        //System.out.println("chunkMessage byteslength: " + bytes.length);
        //System.out.println("chunkMessage chunksize: " + chunkSize);

        for(int i = 0; i <= numberOfChunks; ++i) {
            int start = chunkSize * i;
            int end   = chunkSize * (i+1);
            end = end > bytes.length ? bytes.length : end;
            MessageWithObject msg = new MessageWithObject();
            msg.message = Message.CHUNK;
            msg.object = Arrays.copyOfRange(bytes, start, end);
            chunks.add(msg);
        }
        return chunks;
    }
}
