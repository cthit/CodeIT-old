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
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.io.File;
import java.io.IOException;
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
    private SourceFile sourceFile;

    private Model model;
    private Game game;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public ServerController(String sourcePath) {
        sourceFile = new SourceFile(new File(sourcePath));
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

    /*
    @Override
    public void newFileRecieved(String teamName, File f) {
        String code = null;
        try {
            code = new String(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        code = code.replaceFirst("package\\s+.+?;", "package pong_sample;");
        code = code.replaceFirst("public\\s+class\\s+.+?\\s", "public class " + teamName + " ");
        code = code.replace("System.out", "//System.out");

        try {
            GameMechanic<PongGame,PongMove> pongPaddle = (GameMechanic<PongGame,PongMove>) JavaSourceFromString.compile(code, teamName + ".java", "pong_sample");
            pongPaddle.createTestGame();
            executor.submit(() -> {
                model.handleContributionFromCompetitor(teamName, pongPaddle);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
        c.sendTCP(sourceFile);
    }

}
