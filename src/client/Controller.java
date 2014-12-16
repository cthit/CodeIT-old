package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import network.Connection;

import java.io.File;

public class Controller {

    private Stage stage;
    @FXML private TextField team_name;
    @FXML private TextField address;
    @FXML private TextField port;
    @FXML private TextField project_path;
    @FXML private Label feedback_team_name;
    @FXML private Label feedback_connection;
    @FXML private Label feedback_project_path;
    private Connection connection;

    public void setStageAndDoSetup(Stage stage) {
        this.stage = stage;

        team_name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (! newValue.matches("[\\w\\d(-_.^*')]+")) {
                    team_name.setEffect(new InnerShadow(1000, Color.DARKRED));
                    System.out.println("BAD VALUE!");
                    feedback_team_name.setText("Plz match this -> [\\w\\d(-_.^*')]+");
                    feedback_team_name.setTextFill(Color.DARKRED);
                } else {
                    team_name.setEffect(new InnerShadow(0, Color.WHITE));
                    feedback_team_name.setText("");
                    feedback_team_name.setTextFill(Color.GREEN);
                }
            }
        });


        ChangeListener<String> listener= new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
                    feedback_connection.setText("Wow very address");
                    feedback_connection.setTextFill(Color.GREEN);
                } else {
                    feedback_connection.setText("Nah, plz fex.");
                    feedback_connection.setTextFill(Color.DARKRED);
                }
            }
        };

        address.textProperty().addListener(listener);
    }

    @FXML
    private void browseClicked() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Project Folder");
        File file = directoryChooser.showDialog(stage);
        if (file == null)
            return;
    }

    @FXML
    private void sendCodeClicked() {
        setupConnection();

        connection.sendMessage("RecieveModule");
        /*connection.sendMessage(team_name.getText());
        System.out.println(String.format(
                "%s\n%s\n%s\n%s",
                team_name.getText(),
                address.getText(),
                port.getText(),
                project_path.getText()
        ));
        */
    }

    @FXML
    private void downloadSourcesClicked() {
        setupConnection();
        connection.sendMessage("GiveSources");
    }

    private void setupConnection() {
        if ( connection == null)
            connection = new Connection( address.getText(), Integer.parseInt(port.getText()) );
    }
}
