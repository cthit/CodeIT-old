package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import it.tejp.codeit.common.network.Message;
import java.io.*;

/**
 * Created by Kerp on 19/10/15.
 */
public class ClientConnection extends Listener{

    private Client client = null;
    private String address = null;
    private int port = 0;

    private final int CONNECTION_TIMEOUT = 5000;

    Thread clientThread = null;

    public ClientConnection(String address, int port) {
        this.address = address;
        this.port = port;

        client = new Client();
        client.addListener(this);
        clientThread = new Thread(client);
        clientThread.start();

       //client.addListener(new Listener());
        registerClasses();
    }

    /**
     * Blocking operation that connects to the server.
     * @return a boolean indicating if the connection was successful.
     */
    public boolean connect() {
        try {
            System.out.println("asdasdadsad");
            client.connect(CONNECTION_TIMEOUT, address, port);
            client.sendTCP(Message.REQUEST_SOURCES);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("Connected");
        connection.sendTCP("hej tejp hur är läget?");

    }

    @Override
    public void received(Connection connection, Object object) {
        System.out.println("Received");


    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected");
    }

    public void requestSources() {
        client.sendTCP(Message.REQUEST_SOURCES);
    }


    public Boolean isConnected() {
        return client.isConnected();
    }

    private void registerClasses() {
        Kryo kryo = client.getKryo();
        kryo.register(Message.class);
        kryo.register(File.class);
    }


    public int getPort() {
        return port;
    }

    public String getInetAddress() {
        return address;
    }
}
