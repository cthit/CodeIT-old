package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private InputStream is = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private int bufferSize = 0;


    public ServerConnection() {
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

    }

    public void startServering() {
//        while (true) {
            try {
                serverLoop();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }

    public void serverLoop() throws IOException {

        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Can't accept client connection. ");
        }

        try {
            is = socket.getInputStream();

            bufferSize = socket.getReceiveBufferSize();

            System.out.println("Buffer size: " + bufferSize);
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        byte[] bytes = new byte[512];

        StringBuilder strBuilder = new StringBuilder();

        int count;
        while ((count = is.read(bytes)) > 0) {
            strBuilder.append(new String(bytes, 0, count)); // TODO it's a little bit ugly to use a stringbuilder and still initialize new string objects every time.
        }

        handleMessage(strBuilder.toString());

        is.close();
        socket.close();
    }

    private void handleMessage(String s) {
        String[] splitMessage = s.split("\0");

        if ("RecieveModule".equals(splitMessage[0])) {
            // handle the file that is beeing sent and combine it with the specified team-name
        } else if ("RequestSources".equals(splitMessage[0])) {
            // Send sources jar file to client
        }

    }

    public static void main(String[] args) {
        ServerConnection serverConnection = new ServerConnection();
        serverConnection.startServering();
    }
}