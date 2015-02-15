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
    private int bufferSize = 0;
    private NetworkEventListener networkEventListener;

    private File sourceFile;

    public ServerConnection(File sourceFile, NetworkEventListener networkEventListener) {
        this.networkEventListener = networkEventListener;
        this.sourceFile = sourceFile;
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create server socket. Perhaps server is already running?");
        }

    }

    public void startServering() {
        new Thread(() -> {
            while (true) {
                try {
                    serverLoop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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


        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int count;

        while(is.available() <= 0);

        while (is.available() > 0) {
            count = is.read(bytes);
            byteStream.write(bytes, 0, count);
        }

        handleMessage(byteStream.toString());

        is.close();
        os.close();
        socket.close();
    }

    private void handleMessage(String message) {
        String[] splitMessage = message.split("\0");

        if ("RecieveModule".equals(splitMessage[0])) {
//            for (String s1 : splitMessage) {
//                System.out.println(s1);
//            }
                /**
                 * Get name of team. and create the sourcefile
                 * call for newFileRecievedListener and announce the new file
                 */
            new File("plugin").mkdir();
            String filePath = "plugin/" + splitMessage[1] + ".java";
            System.out.println("NewFilePath: " + filePath);

                try {
                    PrintWriter p = new PrintWriter(filePath, "UTF-8");
                    p.write(splitMessage[2]);
                    p.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                networkEventListener.newFileRecieved(splitMessage[1], new File(filePath));

        } else if ("RequestSources".equals(splitMessage[0])) {
            // Send sources jar file to client
            byte[] bytes = null;
            try {
                bytes = Files.readAllBytes(sourceFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sendData(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                throw null;
            }
        }
    }

    public void sendData(byte[] byteMessage) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        bos.write(byteMessage);
        bos.flush();
        bos.close();
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

}