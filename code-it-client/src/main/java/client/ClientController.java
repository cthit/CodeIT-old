package client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.tejp.codeit.api.GameMechanic;
import it.tejp.codeit.common.network.Message;
import it.tejp.codeit.common.network.MessageWithObject;
import it.tejp.codeit.common.network.Network;
import it.tejp.codeit.common.network.Serializer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.dialog.Dialogs;
import utils.JavaSourceFromString;
import view.AITestScene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ClientController extends Listener {

    private Stage stage;
    @FXML private TextField file_path;
    @FXML private TextField simulation_delay;
    @FXML private Label feedback_project_path;
    @FXML private Label feedback_simulation;

    private Client client = null;
    private byte[] chunks = null;
    private int chunkSize = -1; //Chunk size -1 indicates that currently no chunk transfer is in progress.

    @Override
    public void connected(Connection connection) {
        System.out.println("Connected");
    }

    @Override
    public void received(Connection connection, Object object) {
        System.out.println("Received something");
        if(object instanceof Message) {
            handleMessage((Message)object);
        }else if(object instanceof MessageWithObject) {
            handleMessageWithObject((MessageWithObject) object);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected");
    }

    private void handleMessage(Message msg) {
        if(msg == Message.TRANSFER_ERROR) {
            chunkSize = -1;
            Platform.runLater(() -> errorDialog("Transfer error", "Unexpected error", "Error on transfer, please try again"));
        }
    }

    private void handleMessageWithObject(MessageWithObject msg) {
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
            if(chunks.length == chunkSize) {
                System.out.println("chunks.length == chunkSize");
                try {
                    chunkSize = -1;
                    handleMessageWithObject((MessageWithObject)Serializer.deserialize(chunks));
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
        if(chunkSize == -1) {
            chunkSize = (int)msg.object;
            chunks = null;
        } else {
            Platform.runLater(() -> errorDialog("Transfer error", "", "New chunked transfer initiated while one was already in progress"));
            client.close();
        }

    }

    private void handleDownloadSources(String fileName, byte[] fileContent) {
        Path filePath = Paths.get(fileName);
        try {
            Files.write(filePath, fileContent);
            new File("compiled").mkdir();
            unzipJar("compiled", fileName);
        } catch (IOException e) {
            errorDialog("File error", e.getLocalizedMessage(), "");//e.getStackTrace());
        }
    }

    private void errorDialog(String title, String masthead, String message) {
        Dialogs.create()
                .owner(stage)
                .title(title)
                .masthead(masthead)
                .message(message)
                .showError();
    }

    public void setup(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
        client.addListener(this);

        file_path.setText("");
    }

    /*public void switchToLoginScene() {
        /*URL location = getClass().getResource("login.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 800, 825);
        scene.getStylesheets().add(
                getClass().getResource("main.css").toExternalForm()
        );



        team_name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (! newValue.matches("[a-zA-Z_$][a-zA-Z\\d_$]*")) {
                    team_name.setEffect(new InnerShadow(1000, Color.DARKRED));
                    feedback_team_name.setText("Plz match this -> [a-zA-Z_$][a-zA-Z\\d_$]*");
                    feedback_team_name.setTextFill(Color.DARKRED);
                } else {
                    team_name.setEffect(new InnerShadow(0, Color.WHITE));
                    feedback_team_name.setText("");
                    feedback_team_name.setTextFill(Color.GREEN);
                }
            }
        });


        ChangeListener<String> listener= (observable, oldValue, newValue) -> {
            if (newValue.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
                feedback_connection.setText("Wow very address");
                feedback_connection.setTextFill(Color.GREEN);
            } else {
                feedback_connection.setText("Nah, plz fex.");
                feedback_connection.setTextFill(Color.DARKRED);
            }
        };

        address.textProperty().addListener(listener);


        team_name.setText("qwerty");
        address.setText("10.0.0.237");
        port.setText("7777");
    }*/

    public void switchToMainScene() {
        URL location = getClass().getResource("main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load(location.openStream());
        } catch (IOException e) {
            //FUUUUUUUUUUUU
        }
        Scene scene = new Scene(root, 800, 825);
        scene.getStylesheets().add(
                getClass().getResource("main.css").toExternalForm());

        stage.setScene(scene);

        ChangeListener<String> numberOnly= (observable, oldValue, newValue) -> {
            if (newValue.matches("\\d+\\.?\\d*")) {
                simulation_delay.setEffect(new InnerShadow(0, Color.WHITE));
                feedback_simulation.setText("");
                feedback_simulation.setTextFill(Color.GREEN);
            } else {
                simulation_delay.setEffect(new InnerShadow(1000, Color.DARKRED));
                feedback_simulation.setText("Nah, plz fex.");
                feedback_simulation.setTextFill(Color.DARKRED);
            }
        };

        simulation_delay.textProperty().addListener(numberOnly);
        file_path.setText("pong-challenge/src/main/java/pong_sample/SimplePongPaddle.java");

    }

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

    @FXML
    private void sendCodeClicked() {
        Path filePath = Paths.get(file_path.getText());
        byte[] content = new byte[0];

        try {
            content = Files.readAllBytes(filePath);
        } catch (IOException e) {
            errorDialog("File error", "Couldn't read file", e.getMessage());
        }

        MessageWithObject msg = new MessageWithObject(Message.TRANSFER_SOURCES, content);
        try {
            Network.sendMessageWithObject(client, msg);
        } catch (IOException e) {
            errorDialog("Transfer error", "Couldn't send message", e.getMessage());
        }
    }

    @FXML
    private void downloadSourcesClicked() {
        System.out.println("downloadSourcesClicked()");
        client.sendTCP(Message.REQUEST_SOURCES);
    }

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
