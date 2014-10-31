package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    private Stage stage;
    @FXML private TextField team_name;
    @FXML private TextField adress;
    @FXML private TextField port;
    @FXML private TextField project_path;

    public void setStageAndDoSetup(Stage stage) {
        this.stage = stage;

        team_name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (! newValue.matches("[\\w\\d(-_.^*')]+")) {
                    System.out.println("BAD VALUE!");
                }
            }
        });

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
        System.out.println(String.format(
                "%s\n%s\n%s\n%s",
                team_name.getText(),
                adress.getText(),
                port.getText(),
                project_path.getText()
        ));
    }
}
