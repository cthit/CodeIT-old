package client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Created by kerp on 20/11/15.
 */
public class LoginController extends Listener {

    @FXML private TextField team_name;
    @FXML private TextField address;
    @FXML private TextField port;
    @FXML private Label feedback_team_name;
    @FXML private Label feedback_connection;

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
        address.setText("10.0.0.237");
        port.setText("7777");
    }

    public void initNetwork() {
        client = new Client();
        //   client.addListener();
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

    @FXML
    private void connectClicked() {
        if(connect(getAddress(), getPort())) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene scene = null;
            try {
               scene = new Scene((Pane)loader.load());
            } catch (IOException e) {

            }
            ClientController controller = loader.<ClientController>getController();
            controller.setup(stage, client);
            scene.getStylesheets().add(
                    getClass().getResource("main.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } else {

        }
    }

    public String getAddress() {
        return address.getText();
    }

    public int getPort() {
        return Integer.parseInt(port.getText());
    }


}
