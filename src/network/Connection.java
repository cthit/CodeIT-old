package network;

import java.io.IOException;
import java.net.*;

/**
 * Created by tejp on 31/10/14.
 */
public class Connection {

    private final InetAddress inetAddress;
    private final int port;
    private DatagramSocket ds;

    public Connection(String address, int port) {
        this.port = port;
        try {
            this.inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown Host: " + address + ":" + port, e);
        }
    }

    public String getPort() {
        return port+"";
    }

    public String getInetAddress() {
        return inetAddress.getHostAddress();
    }

    public void sendMessage(String message) {
        try {
            ds = new DatagramSocket();

            byte[] byteMessage = message.getBytes();

            DatagramPacket dp = new DatagramPacket(byteMessage, byteMessage.length, inetAddress, port);
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        ds.close();
    }

}
