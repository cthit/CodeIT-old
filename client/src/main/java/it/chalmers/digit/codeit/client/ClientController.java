package it.chalmers.digit.codeit.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.chalmers.digit.codeit.api.GameMechanic;
import it.chalmers.digit.codeit.client.view.AITestScene;
import it.chalmers.digit.codeit.common.network.Message;
import it.chalmers.digit.codeit.common.network.MessageWithObject;
import it.chalmers.digit.codeit.common.network.Network;
import it.chalmers.digit.codeit.common.network.Serializer;
import it.chalmers.digit.codeit.server.utils.JavaSourceFromString;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;


public class ClientController extends Listener {

    private static final Logger log = Logger.getLogger(ClientController.class.getName());

    private Stage stage;
    @FXML private TextField file_path;
    @FXML private TextField simulation_delay;
    @FXML private Label feedback_project_path;
    @FXML private Label feedback_simulation;

    @FXML private Label team_name;
    @FXML private Label server_status;

    @FXML private Button send_code;
    @FXML private Button download_sources;
    @FXML private Button test_ai;
    @FXML private Button reconnect;

    //Where to put the unzipped source downloaded from the server.
    private final String JAR_DIRECTORY = "compiled/";

    private Client client = null;
    private byte[] chunks = null;
    private int chunkSize = -1; //Chunk size -1 indicates that currently no chunk transfer is in progress.

    /**
     * Callback method when a connection is established.
     * @param connection The connection that was established.
     */
    @Override
    public void connected(Connection connection) {
        log.info("Connected to " + connection.getRemoteAddressTCP());
        Platform.runLater(() -> setServerStatus("Connected to " + client.getRemoteAddressTCP(), Color.DARKRED));
        Platform.runLater(() -> send_code.setDisable(false));
        Platform.runLater(() -> download_sources.setDisable(false));
        Platform.runLater(() -> reconnect.setVisible(false));
    }

    /**
     * Callback when data is received on a connection.
     * @param connection The connection on which the data is received .
     * @param object The data received.
     */
    @Override
    public void received(Connection connection, Object object) {
        if(object instanceof Message) {
            handleMessage((Message)object);
        }else if(object instanceof MessageWithObject) {
            handleMessageWithObject((MessageWithObject) object);
        }
    }

    /**
     * Callback when a connection is disconnected.
     * @param connection The connection that was disconnected.
     */
    @Override
    public void disconnected(Connection connection) {
        log.info("Disconnected " + connection.getRemoteAddressTCP());
        Platform.runLater(() -> setServerStatus("Disconnected from server", Color.DARKRED));
        Platform.runLater(() -> send_code.setDisable(true));
        Platform.runLater(() -> download_sources.setDisable(true));
        Platform.runLater(() -> reconnect.setVisible(true));
    }

    /**
     * Handles messages śent by the server.
     * @param msg The message sent by the server.
     */
    private void handleMessage(Message msg) {
        if(msg == Message.TRANSFER_ERROR) {
            chunkSize = -1;
            Platform.runLater(() -> errorDialog("Transfer error", "Unexpected error", "Error on transfer, please try again"));
        }
    }

    /**
     * Handles messages that also contains objects that are sent by the server.
     * @param msg The message with an object attached.
     */
    private void handleMessageWithObject(MessageWithObject msg) {
        log.info("Received: " + msg.message);
        if (msg.message == Message.TRANSFER_SOURCES) {
            handleDownloadSources("source.jar", (byte[]) msg.object);
        } else if(msg.message == Message.CHUNKED_TRANSFER) {
            handleNewChunkedTransfer(msg);
        } else if(msg.message == Message.CHUNK) {
            handleChunk(msg);
        }
    }

    /**
     * Handles a single chunk, to allow any chunks from being received a chunk transfer has first be initiated.
     * @param msg contains message CHUNK and a byte array that's a part of a MessageWithObject
     */
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

    /**y
     * Writes a file with the specified name and contents to disk and unzips it into a directory named 'compiled'
     * @param fileName The name of the file on disk.
     * @param fileContent The content of the file.
     */
    private void handleDownloadSources(String fileName, byte[] fileContent) {
        Path filePath = Paths.get(JAR_DIRECTORY + fileName);
        try {
            new File(JAR_DIRECTORY).mkdir();
            Files.write(filePath, fileContent);
            unzipJar(JAR_DIRECTORY, filePath.toString());
            Platform.runLater(() -> test_ai.setDisable(false));
            Platform.runLater(() -> setServerStatus("Connected to " + client.getRemoteAddressTCP(), Color.GREEN));
        } catch (IOException e) {
            Platform.runLater(() -> errorDialog("File error", "Couldn't write downloaded sources to disk", e.getLocalizedMessage()));
        }
    }

    /**
     * Displays an dialog box with the error template to the user.
     * Needs to be invoked with Platform.runlater(() -> ) in the network thread.
     * @param title The title of the dialog box.
     * @param masthead The masthea dof the dialog box.
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

    /**
     * Should be called when this scene is created, handles setup tasks.
     * @param stage The stage that this scene belongs to.
     * @param client The client that has an open connection to the server.
     * @param teamName The teamname that the player has chosen.
     */
    public void setup(Stage stage, Client client, String teamName) {
        this.stage = stage;
        this.client = client;
        this.team_name.setText(teamName);
        client.addListener(this);

        setServerStatus("Connected to " + client.getRemoteAddressTCP(), Color.GREEN);
        test_ai.setDisable(true); //Set to disabled until sources have been downloaded from the server.
        reconnect.setVisible(false);

        file_path.setText("/home/kalior/project/codeit/pong-challenge/src/main/java/pong_sample/SimplePongPaddle.java");
    }

    /**
     * Updates the server status label.
     * @param text The text to display.
     * @param color The color of the text.
     */
    private void setServerStatus(String text, Paint color) {
        server_status.setText(text);
        server_status.setTextFill(color);
    }

    /**
     * Handles the switch to the login scene.
     * Loads the correct fxml layout, calls setup() on the correct controller and removes this class as listener
     * on network events.
     */
    public void switchToLoginScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene((Pane)loader.load());
        } catch (IOException e) {
            errorDialog("Load error", "Couldn't load layout file", e.getMessage());
        }

        stage.setScene(scene);
        client.removeListener(this);
        LoginController controller = loader.<LoginController>getController();
        controller.setup(stage);
        stage.show();
    }

    /**
     * Handle the onClickEvent on the reconnect button.
     */
    @FXML
    private void onReconnectClicked() {
        switchToLoginScene();
    }
    /**
     * Handle the onClickEvent on the file browse button.
     */
    @FXML
    private void browseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your AI file");
        File file = new File(file_path.getText());
        if(file.exists() && file.getParentFile().isDirectory()) {
            fileChooser.setInitialDirectory(file.getParentFile());
        }
        File choosenFile = fileChooser.showOpenDialog(stage);
        if (choosenFile != null)
            file_path.setText(choosenFile.getAbsolutePath());
    }

    /**
     * Handles the onClickEvent on the send code button.
     */
    @FXML
    private void sendCodeClicked() {
        Path filePath = Paths.get(file_path.getText());
        byte[] content = new byte[0];

        try {
            content = Files.readAllBytes(filePath);
        } catch (IOException e) {
            errorDialog("File error", "Couldn't read file", e.getMessage());
            return;
        }

        MessageWithObject msg = new MessageWithObject(Message.TRANSFER_SOURCES, content);
        try {
            Network.sendMessageWithObject(client, msg);
        } catch (IOException e) {
            errorDialog("Transfer error", "Couldn't send message", e.getMessage());
        }
    }

    /**
     * Handles the onClickEvent on the download sources button.
     */
    @FXML
    private void downloadSourcesClicked() {
        client.sendTCP(Message.REQUEST_SOURCES);
        setServerStatus("Downloading sources", Color.GREEN);
    }

    /**
     * Handles the onClickEvent on the test AI button.
     */
    @FXML
    private void testMyAIClicked() {
        File file = new File(file_path.getText());
        String code = null;
        Object instanceObj = null;
        double delay = 0;
        try {
            delay = new Double(simulation_delay.getText()).doubleValue();
        } catch(NumberFormatException e) {
            errorDialog("Number error", "Not a number", e.getLocalizedMessage());
            return;
        }
        try {
            code = new String(Files.readAllBytes(file.toPath()));

        } catch (IOException e) {
            errorDialog("File error", "Couldn't read file", "Path: " + file_path.getText());
            return;
        }
        code = code.replaceFirst("package\\s+.+?;", "package pong_sample;");
        try {
            instanceObj = JavaSourceFromString.compile(code, file.getName(), "pong_sample");
        } catch (RuntimeException e) {
            errorDialog("Compiler error", "Couldn't compile class", "Error: " + e.getMessage());
            return;
        }
        if (instanceObj == null) {
            errorDialog("Compiler error", "Couldn't instantiate class", "instanceObj == null");
            return;
        }
        GameMechanic<?,?> instance = (GameMechanic<?,?>)instanceObj;
        Stage stage = new Stage();
        AITestScene testScene = new AITestScene(instance, delay);
        stage.setScene(testScene);
        stage.show();
        testScene.play();
    }

    /**
     * Unzips the given jar file into a directory.
     * @param destinationDir The directory where the jar file will be unzipped.
     * @param jarPath THe path to the jar file.
     * @throws IOException
     */
    public static void unzipJar(String destinationDir, String jarPath) throws IOException {
        File file = new File(jarPath);
        JarFile jar = new JarFile(file);
        // fist get all directories,
        // then make those directory on the destination Path
        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
            JarEntry entry = enums.nextElement();

            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);

            if (fileName.endsWith("/")) {
                f.mkdirs();
            }

        }

        //now create all files
        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
            JarEntry entry = enums.nextElement();

            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);

            if (!fileName.endsWith("/")) {
                InputStream is = jar.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(f);

                // write contents of 'is' to 'fos'
                while (is.available() > 0) {
                    fos.write(is.read());
                }

                fos.close();
                is.close();
            }
        }
    }
}
