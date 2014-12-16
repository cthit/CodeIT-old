package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by tejp on 31/10/14.
 */
public class ServerConnection {

    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(7777);

            byte[] buffer = new byte[65535];
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);

            while(true) {
                socket.receive(incomingPacket);
                byte[] data = incomingPacket.getData();
                String s = new String(data, 0, incomingPacket.getLength());

                System.out.println(incomingPacket.getAddress() + " : " + incomingPacket.getPort() + " - " + s);

                if ("GiveSources".equals(s)){
                    System.out.println("GiveSources");
                } else if ("RecieveModule".equals(s)){
                    System.out.println("RevieceModule");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
