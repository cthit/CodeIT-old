package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class ServerConnection {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private InputStream is = null;
    private OutputStream os = null;
//    private FileOutputStream fos = null;
//    private BufferedOutputStream bos = null;
    private int bufferSize = 0;

    private File sourceFile;

    public ServerConnection(File sourceFile) {
        this.sourceFile = sourceFile;
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

    }

    public void startServering() {
        while (true) {
            try {
                serverLoop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            os = socket.getOutputStream();

            bufferSize = socket.getReceiveBufferSize();

            System.out.println("Buffer size: " + bufferSize);
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        byte[] bytes = new byte[512];

        StringBuilder strBuilder = new StringBuilder();

        int count;
//        while ((count = is.read(bytes)) > 0) {
//            strBuilder.append(new String(bytes, 0, count)); // TODO it's a little bit ugly to use a stringbuilder and still initialize new string objects every time.
//        }

        do {
            count = is.read(bytes);
            strBuilder.append(new String(bytes,0,count));
        } while (count > 0);

        handleMessage(strBuilder.toString());

        is.close();
        os.close();
        socket.close();
    }

    private void handleMessage(String message) {
        String[] splitMessage = message.split("\0");

        if ("RecieveModule".equals(splitMessage[0])) {
            for (String s1 : splitMessage) {
                System.out.println(s1);
            }
            // handle the file that is beeing sent and combine it with the specified team-name
        } else if ("RequestSources".equals(splitMessage[0])) {
            // Send sources jar file to client
            String s = null;
            try {
                s = new String(Files.readAllBytes(sourceFile.toPath()));

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sendData(s.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw null;
            }
        }
    }

    public void sendData(byte[] byteMessage) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        for (int i = 0 ; i < byteMessage.length ; i++) {
            bos.write(byteMessage[i]);
        }
        bos.flush();
        bos.close();
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public static void main(String[] args) {
        ServerConnection serverConnection = new ServerConnection(new File("/home/tejp/SourceFile"));
        serverConnection.startServering();
    }
}