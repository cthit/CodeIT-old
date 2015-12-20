package it.tejp.codeit.common.network;

import com.esotericsoftware.kryonet.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions for communication over the network.
 */
public class Network {

    /**
     * Sends the given MessageWithObject to a connection, makes sure that the message can be sent regardless of size.
     * @param connection The connection to send the message on.
     * @param msg The message that is to be sent.
     * @throws IOException Throws an exception if the message can't be serialized.
     */
    public static void sendMessageWithObject(Connection connection, MessageWithObject msg) throws IOException {
        int bufferSize = connection.getTcpWriteBufferSize();
        byte[] content = Serializer.serialize(msg);
        int contentSize = content.length;

        List<MessageWithObject> chunks = chunkMessage(content, bufferSize);

        MessageWithObject chunkedTransfer = new MessageWithObject(Message.CHUNKED_TRANSFER, contentSize);
        connection.sendTCP(chunkedTransfer);

        for(MessageWithObject chunk : chunks) {
            connection.sendTCP(chunk);
        }
    }

    /**
     * Takes a byte array and splits it up in chunks that are guaranteed to be less than or equal to chunkSize in size.
     * @param bytes The byte array to split up
     * @param chunkSize The maximum size of the array parts.
     * @return
     */
    private static List<MessageWithObject> chunkMessage(byte[] bytes, int chunkSize) {
        int numberOfChunks = (int)Math.ceil(bytes.length / chunkSize);
        List<MessageWithObject> chunks = new ArrayList<>();

        for(int i = 0; i < numberOfChunks; ++i) {
            MessageWithObject msg = new MessageWithObject();
            msg.message = Message.CHUNK;
            msg.object = Arrays.copyOfRange(bytes, chunkSize * i, chunkSize * (i + 1));
            chunks.add(msg);
        }
        return chunks;
    }
}
