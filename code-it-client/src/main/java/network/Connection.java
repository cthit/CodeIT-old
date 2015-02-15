package network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

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
            throw new RuntimeException("Couldn't connect to server");
        }
    }

    public void recieveSources() {
        try {
            socket = new Socket(inetAddress, port);
            sendData("RequestSources".getBytes());

            InputStream is = socket.getInputStream();
            byte[] bytes = new byte[512];


            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int count;
            while ((count = is.read(bytes)) > 0) {
                byteStream.write(bytes, 0, count);
            }
            is.close();
            socket.close();

            Files.write(new File("source.jar").toPath(), byteStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Couldn't connect to server");
        }
    }

    public void sendData(byte[] byteMessage) throws IOException {
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        for (int i = 0 ; i < byteMessage.length ; i++) {
            bos.write(byteMessage[i]);
        }
        bos.flush();

    }
}
