package it.chalmers.digit.codeit.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import it.chalmers.digit.codeit.api.Competitor;
import it.chalmers.digit.codeit.api.Game;
import it.chalmers.digit.codeit.api.GameMechanic;
import it.chalmers.digit.codeit.common.network.*;
import it.chalmers.digit.codeit.common.utils.JavaCompilerHelper;
import it.chalmers.digit.codeit.server.game.ChunkBuffer;
import it.chalmers.digit.codeit.server.game.Model;
import it.chalmers.digit.codeit.server.game.Team;
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * ServerController class
 * handles connections and all responses to clients
 */
public class ServerController extends Listener {

    private static final Logger log = Logger.getLogger(ServerController.class.getName());

    private Server server = new Server(Network.BUFFER_SIZE, Network.BUFFER_SIZE);
    private Map<Connection, String> connectedTeams = new HashMap<>();
    private Map<String, ChunkBuffer> buffers = new HashMap<>();

    //private Game game;
    private Model model;
    /* Old variables, unclear where they're needed.
    */
    private ExecutorService executor = Executors.newCachedThreadPool();


    //public ServerController(Game game, String sourcePath) {
    public ServerController() {
        //this.game = game;
        server.addListener(this);
    }


    /**
     * start the server in the controller
     */
    public void start() {
        Initializer.registerClasses(server.getKryo());
        server.start();
        try {
            server.bind(7777);
        } catch (IOException e) {
            log.info("Could not bind to port: 7777, is the server already running?");
        }

        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a, b) -> new PongGame(a, b);
        model = new Model(gameFactory);

    }

    /**
     * disconnects a client
     * @param connection a client connection to be disconnected
     */
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

    /**
     *
     * @param conn a connected client
     * @param message the message
     */
    private void handleMessage(Connection conn, Message message) {
        if (message == Message.REQUEST_SOURCES) {
            sendSources(conn);
        } else if(message == Message.REQUEST_RATINGS) {
            sendRatings(conn);
        }
    }

    /**
     * forwards a message to apropriate handle method for that message
     * @param conn a connected client
     * @param m the Message with an attached object
     */
    private void handleMessageWithObject(Connection conn, MessageWithObject m) {

        switch (m.message) {
            case NEW_TEAMNAME:
                handleNewTeamName(conn, (String) m.object);
                break;
            case TRANSFER_SOURCES:
                receiveSourceFromTeam(conn, m);
                break;
            case CHUNKED_TRANSFER:
                handleNewChunkedTransfer(conn, m);
                break;
            case CHUNK:
                handleChunk(conn, m);
                break;
            case TRANSFER_ERROR:
                handleTransferError(conn, m);
                break;
            default:
                log.info("Received unknown message from" + connectedTeams.get(conn));
        }
    }

    /**
     * handles a transfer error
     * @param conn a connected client
     * @param m the Message with an attached object
     */
    private void handleTransferError(Connection conn, MessageWithObject m) {
        log.info("Transfer error from: " + connectedTeams.get(conn));
        log.info("Message: " + m.message);
    }

    /**
     * handles a new incoming chunk. appends it to that connections previous chunks and sends a new message if that was the last chunk
     * @param conn a connected client
     * @param m the message with an attached object
     */
    private void handleChunk(Connection conn, MessageWithObject m) {
        String teamName = connectedTeams.get(conn);
        ChunkBuffer cb = buffers.get(teamName);
        byte[] chunk = (byte[]) m.object;

        cb.addChunk(chunk);

        if (cb.getExpectedSizeOfChunks() == cb.getChunks().length) {
            MessageWithObject chunkM = null;
            try {
                chunkM = (MessageWithObject) Serializer.deserialize(cb.getChunks());
            } catch (IOException | ClassNotFoundException e) {
                log.info("Could not deserialize chunk buffer");
                e.printStackTrace();
            }
            handleMessageWithObject(conn, chunkM);
        }

    }

    /**
     * a message which initialized a new chunked transfer is received.
     * sets the expected size of all chunks and clears all erlier chunks
     * @param conn a connection client
     * @param m the message with an attached object
     */
    private void handleNewChunkedTransfer(Connection conn, MessageWithObject m) {
        log.info(conn + "");
        String t = connectedTeams.get(conn);
        int expectedSize = (int) m.object;
        buffers.put(t, new ChunkBuffer(expectedSize));
    }


    /**
     *
     * @param c a connected client
     * @param teamName
     */
    private void handleNewTeamName(Connection c, String teamName) {
        List<String> teamNames = connectedTeams.values().stream().collect(Collectors.toList());

        if ( teamNames.contains(teamName) ) {
            c.sendTCP(Message.BAD_TEAMNAME);
            c.close();
        } else {
            connectedTeams.put(c, teamName);
            c.sendTCP(Message.GOOD_TEAMNAME);
        }
    }

    /**
     * sends source file to the client
     * @param c a connected client
     */
    private void sendSources(Connection c) {
        System.out.println("Fuck you");
//        try {
//            byte[] content = Files.readAllBytes(sourceFilePath);
//            MessageWithObject m = new MessageWithObject(Message.TRANSFER_SOURCES, content);
//            Network.sendMessageWithObject(c, m);
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.info("Could not find file to send to client. " + sourceFilePath);
//        }
    }

    /**
     * Sends the list of teams and their ratings to a connection
     * @param conn the connection to send to
     */
    private void sendRatings(Connection conn) {
        Map<String, Double> teamRatings = model.getRating();
        MessageWithObject msg = new MessageWithObject(Message.TRANSFER_RATINGS, teamRatings);
        try {
            Network.sendMessageWithObject(conn, msg);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Could not send ratings to ratingvisualizer");
        }
    }

    /**
     * receives and
     * @param c a connected client
     * @param m a message with a attached object
     */
    private void receiveSourceFromTeam(Connection c, MessageWithObject m) {
        byte[] bytes = (byte[]) m.object;

        String code = null;

        try {
            code = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.info("Couldn't handle encoding");
            e.printStackTrace();
        }

        try {
            String teamName = connectedTeams.get(c);
            GameMechanic gameMechanic = (GameMechanic)JavaCompilerHelper.compile(code);
            log.info(String.format("Received new source from %s", teamName));

            executor.execute(() -> {
                model.handleContributionFromCompetitor(teamName, gameMechanic);
                log.info(model.getRating() + "");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
