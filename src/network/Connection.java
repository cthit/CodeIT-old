package network;

import java.io.IOException;
import java.net.*;

/**
 * Created by tejp on 31/10/14.
 */
public class Connection {

    private final InetAddress inetAddress;
    private final int port;

    public Connection(String address, int port) {
        this.port = port;
        try {
            this.inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown Host: " + address + ":" + port, e);
        }
    }

    public void sendMessage(String message) {
        try {
            DatagramSocket ds = new DatagramSocket();


            byte[] byteMessage = message.getBytes();

            DatagramPacket dp = new DatagramPacket(byteMessage, byteMessage.length, inetAddress, port);
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
