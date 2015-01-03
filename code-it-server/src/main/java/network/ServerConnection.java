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
    private NewFileFromClientListener newFileFromClientListener;

    private File sourceFile;

    public ServerConnection(File sourceFile, NewFileFromClientListener newFileFromClientListener) {
        this.newFileFromClientListener = newFileFromClientListener;
        this.sourceFile = sourceFile;
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
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

        while ((count = is.read(bytes)) > 0) {
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

                newFileFromClientListener.newFileRecieved(splitMessage[1], new File(filePath));

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

}