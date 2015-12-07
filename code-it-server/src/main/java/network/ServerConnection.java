package network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import it.tejp.codeit.common.network.Message;
import network.listeners.SendSourceOnConnectListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by tejp on 19/10/15.
 */
public class ServerConnection {

    private Server server = new Server();
    private int tcpPort;

    public ServerConnection(int tcpPort) {
        server.getKryo().register(Message.class);
        this.tcpPort = tcpPort;
    }

    public void startServer() throws IOException {
        server.start();
        server.bind(tcpPort);
    }

    public void addListener(Listener listener) {
        server.addListener(listener);
    }

    public static void main(String[] args) {

        ServerConnection sc = new ServerConnection(4242);

        sc.addListener(new SendSourceOnConnectListener(new File("README.md")));

        try {
            sc.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("apabepa");
        sc.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                System.out.println("message received");
                if (object instanceof Message) {
                    Message request = (Message) object;
                    System.out.println(request.message);

                    Message response = Message.REQUEST_SOURCES;
                    connection.sendTCP(response);
                }
            }
        });
    }

}