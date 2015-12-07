package network;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import it.tejp.codeit.common.network.Initializer;

import java.io.IOException;

/**
 * Created by tejp on 19/10/15.
 */
public class ServerConnection {

    private Server server = new Server();
    private int tcpPort;

    public ServerConnection(int tcpPort) {
        //server.getKryo().register(Message.class);
        Initializer.registerClasses(server.getKryo());
        this.tcpPort = tcpPort;
    }

    public void startServer() throws IOException {
        server.start();
        server.bind(tcpPort);
    }

    public void addListener(Listener listener) {
        server.addListener(listener);
    }
}