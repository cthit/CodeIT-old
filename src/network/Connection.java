package network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

/**
 * Created by tejp on 31/10/14.
 */
public class Connection {

    private final InetAddress inetAddress;
    private final int port;
    private Socket socket;

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
            socket = new Socket(inetAddress, port);
            sendData(message.getBytes());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] byteMessage) throws IOException {
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        for (int i = 0 ; i < byteMessage.length ; i++) {
            bos.write(byteMessage[i]);
        }
        bos.flush();

        bos.close();
        os.close();
    }

}
