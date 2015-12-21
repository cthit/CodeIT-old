package it.chalmers.digit.codeit.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.chalmers.digit.codeit.common.network.Initializer;
import it.chalmers.digit.codeit.common.network.Message;
import it.chalmers.digit.codeit.common.network.MessageWithObject;
import it.chalmers.digit.codeit.common.network.Network;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by kerp on 20/11/15.
 */
public class LoginController extends Listener {

    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField team_name;
    @FXML private TextField address;
    @FXML private TextField port;
    @FXML private Label feedback_team_name;
    @FXML private Label feedback_connection;
    @FXML private Label feedback_connecting;

    //Network stuff.
    private Client client = null;
    private final int CONNECTION_TIMEOUT = 5000;
    Thread clientThread = null;

    private Stage stage = null;

    public void setup(Stage stage) {
        this.stage = stage;
        initNetwork();

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
        address.setText("192.168.1.6");
        port.setText("7777");
    }

    public void initNetwork() {
        client = new Client(Network.BUFFER_SIZE, Network.BUFFER_SIZE);

        Initializer.registerClasses(client.getKryo());
        client.addListener(this);
        clientThread = new Thread(client);
        clientThread.start();
    }

    public boolean connect(String address, int port) {
        try {
            client.connect(CONNECTION_TIMEOUT, address, port);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void connected(Connection connection) {
        log.info("Connected to " + connection.getRemoteAddressTCP());
        MessageWithObject newTeamName = new MessageWithObject(Message.NEW_TEAMNAME, team_name.getText());
        connection.sendTCP(newTeamName);
    }

    @Override
    public void received(Connection connection, Object object) {
        log.info("Received message from " + connection.getRemoteAddressTCP());

        if(object instanceof Message) {
            handleMessage((Message)object);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        log.info("Disconnected from " + connection.getRemoteAddressTCP());
    }

    private void handleMessage(Message msg) {
        log.info("Message: " + msg);
        if(msg == Message.GOOD_TEAMNAME) {
            Platform.runLater(() -> switchToMainScene());
        }else if(msg == Message.BAD_TEAMNAME) {
            Platform.runLater(() -> setBadTeamName());
        }
    }

    @FXML
    private void connectClicked() {
        setConnectingFeedback("Trying to connect");
        if(connect(getAddress(), getPort())) {
            setConnectingFeedback("");
        } else {
            setConnectingFeedback("Couldn't connect to server");
        }
    }

    private void setBadTeamName() {
        feedback_team_name.setText("Team name already connected to server.");
    }

    private void setConnectingFeedback(String text) {
        feedback_connection.setText(text);
    }

    private void switchToMainScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/main.fxml"));
        Scene scene = null;
        try {
            scene = new Scene((Pane)loader.load());
        } catch (IOException e) {

        }
        ClientController controller = loader.<ClientController>getController();
        client.removeListener(this);
        controller.setup(stage, client);
        scene.getStylesheets().add(
                getClass().getResource("view/main.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public String getAddress() {
        return address.getText();
    }

    public int getPort() {
        return Integer.parseInt(port.getText());
    }


}
