package it.chalmers.digit.codeit.ratingvisualizer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.chalmers.digit.codeit.common.network.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by tejp on 16/02/15.
 */
@SuppressWarnings("Duplicates")
public class RatingVisualizerController extends Listener {

    private static final Logger log = Logger.getLogger(RatingVisualizer.class.getName());

    private int port;
    private InetAddress address;

    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    @FXML private TableView<TeamRatingEntry> table;
    private Stage stage;

    private Client client = null;
    private byte[] chunks = null;
    Thread clientThread = null;
    private int chunkSize = -1; //Chunk size -1 indicates that currently no chunk transfer is in progress.

    public void initNetwork() {
        client = new Client(Network.BUFFER_SIZE, Network.BUFFER_SIZE);

        Initializer.registerClasses(client.getKryo());
        client.addListener(this);
        clientThread = new Thread(client);
        clientThread.start();
        try {
            client.connect(5000, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(Connection connection) {
        log.info("Connected to " + connection.getRemoteAddressTCP());
    }

    /**
     * Callback when data is received on a connection.
     * @param connection The connection on which the data is received .
     * @param object The data received.
     */
    @Override
    public void received(Connection connection, Object object) {
        if(object instanceof MessageWithObject) {
            handleMessageWithObject((MessageWithObject) object);
        }
    }

    private void handleMessageWithObject(MessageWithObject msg) {
        log.info("Received: " + msg.message);
        if(msg.message == Message.CHUNKED_TRANSFER) {
            handleNewChunkedTransfer(msg);
        } else if(msg.message == Message.CHUNK) {
            handleChunk(msg);
        } else if(msg.message == Message.TRANSFER_RATINGS) {
            handleTransferRatings(msg);
        }
    }

    private void handleTransferRatings(MessageWithObject msg) {
        final ObservableList<TeamRatingEntry> items = table.getItems();

        Platform.runLater(() -> {
            items.clear();
            Map<String, Double> ratings = (Map<String, Double>)msg.object;
            ratings.entrySet().forEach(e -> {
                items.add(new TeamRatingEntry(
                        e.getKey(),
                        e.getValue()
                ));
            });
        });
    }

    private void handleChunk(MessageWithObject msg) {
        if(chunkSize == -1) {
            Platform.runLater(() -> errorDialog("Transfer error", "", "Got chunk while no chunk transfer was in progress"));
        } else {
            chunks = ArrayUtils.addAll(chunks, (byte[])msg.object);
            log.info("Chunksize: " + chunks.length);
            if(chunks.length == chunkSize) {
                log.info("chunks.length == chunkSize");
                try {
                    chunkSize = -1;
                    handleMessageWithObject((MessageWithObject) Serializer.deserialize(chunks));
                } catch (IOException e) {
                    Platform.runLater(() -> errorDialog("Transfer error", e.getMessage(), "Couldn't deserialize a received message"));
                } catch (ClassNotFoundException e) {
                    Platform.runLater(() -> errorDialog("Transfer error", e.getMessage(), "Couldn't deserialize chunked transfer in a meaningfull way"));
                }
            } else if(chunks.length > chunkSize) {
                Platform.runLater(() -> errorDialog("Transfer error", "Received too many bytes in a chunked transfer", "chunks.length > chunkSize"));
            }
        }
    }

    /**
     * Initiates a new chunk transfer, only one chunk transfer can can happen at a time.
     * @param msg The message containing the expected size of all the chunks.
     */
    private void handleNewChunkedTransfer(MessageWithObject msg) {
        log.info("handleNewChunkedTransfer");
        if(chunkSize == -1) {
            chunkSize = (int)msg.object;
            chunks = null;
        } else {
            Platform.runLater(() -> errorDialog("Transfer error", "", "New chunked transfer initiated while one was already in progress"));
            client.close();
        }
    }

    /**
     * Callback when a connection is disconnected.
     * @param connection The connection that was disconnected.
     */
    @Override
    public void disconnected(Connection connection) {
        log.info("Disconnected " + connection.getRemoteAddressTCP());
    }

    public void startDataWatcher() {
        scheduledExecutor.scheduleAtFixedRate(this::requestRatingFromServer, 0, 5, TimeUnit.SECONDS);
    }

    public void requestRatingFromServer() {
        client.sendTCP(Message.REQUEST_RATINGS);
    }

    public void initialize(String address, int port, Stage stage) {
        this.port = port;
        this.stage = stage;
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException(String.format("Could not find host ip address: %s", address));
        }
        initNetwork();
        startDataWatcher();
    }

    /**
     * Displays an dialog box with the error template to the user.
     * Needs to be invoked with Platform.runlater(() -> ) in the network thread.
     * @param title The title of the dialog box.
     * @param masthead The masthead dof the dialog box.
     * @param message The message of the dialog box.
     */
    private void errorDialog(String title, String masthead, String message) {
        Dialogs.create()
                .owner(stage)
                .title(title)
                .masthead(masthead)
                .message(message)
                .showError();
    }

    public static class TeamRatingEntry {
        private final SimpleStringProperty teamName;
        private final SimpleDoubleProperty rating;

        public TeamRatingEntry(String teamName, double rating) {
            this.teamName = new SimpleStringProperty(teamName);
            this.rating = new SimpleDoubleProperty(rating);
        }

        public String getTeamName() {
            return teamName.get();
        }

        public void setTeamName(String teamName) {
            this.teamName.set(teamName);
        }

        public double getRating() {
            return rating.get();
        }


        public void setRating(double rating) {
            this.rating.set(rating);
        }
    }
}
