package game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import it.tejp.codeit.common.network.Initializer;
import it.tejp.codeit.common.network.Message;
import it.tejp.codeit.common.network.MessageWithObject;
import it.tejp.codeit.common.network.SourceFile;
import network.ServerConnection;
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

/**
 * ServerController class
 * handles connections and all responses to clients
 */
public class ServerController extends Listener {

    private Server server = new Server();
    private HashMap<Connection, String> connectedTeams = new HashMap<>();
    //private SourceFile sourceFile;
    private Path sourceFilePath;

    private ServerConnection connection;
    private Model model;
    private Game game;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public ServerController(String sourcePath) {
        sourceFilePath = Paths.get(sourcePath);
        server.addListener(this);
        this.connection = new ServerConnection(7777);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void start() {
        Initializer.registerClasses(server.getKryo());
        server.start();
        try {
            server.bind(7777);
        } catch (IOException e) {
            System.out.println("Could not bind to port. Is port busy? Maybe by another server instance.");
        }

        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a, b) -> new PongGame(a, b);
        model = new Model(gameFactory);

    }

    @Override
    public void disconnected(Connection connection) {
        connectedTeams.remove(connection);
    }

    @Override
    public void received(Connection conn, Object object) {
        if (object instanceof Message) {
            handleMessage(conn, (Message) object);
        } else if (object instanceof MessageWithObject) {
            handleMessageWithObject(conn, (MessageWithObject) object);
        }
    }

    private void handleMessage(Connection connection, Message message) {
        if (message == Message.REQUEST_SOURCES) {
            sendSources(connection);
        }
    }

    private void handleMessageWithObject(Connection connection, MessageWithObject m) {
        if (m.message == Message.NEW_TEAMNAME) {
            handleNewTeamName(connection, (String) m.object);
        }
    }

    private void handleNewTeamName(Connection c, String teamName) {
        if ( connectedTeams.containsValue(teamName) ) {
            c.sendTCP(Message.BAD_TEAMNAME);
        }
        else {
            connectedTeams.put(c, teamName);
            c.sendTCP(Message.GOOD_TEAMNAME);
        }
    }

    private void sendSources(Connection c) {
        try {
            byte[] content = Files.readAllBytes(sourceFilePath);
            MessageWithObject m = new MessageWithObject(Message.TRANSFER_SOURCES, content);
            c.sendTCP(m);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not find file to send to client. " + sourceFilePath);
        }
    }

}
