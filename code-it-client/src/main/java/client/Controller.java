package client;

import it.tejp.codeit.api.GameMechanic;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import network.Connection;
import utils.JavaSourceFromString;
import view.AITestScene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Controller {

    private Stage stage;
    @FXML private TextField team_name;
    @FXML private TextField address;
    @FXML private TextField port;
    @FXML private TextField file_path;
    @FXML private Label feedback_team_name;
    @FXML private Label feedback_connection;
    @FXML private Label feedback_project_path;
    private Connection connection;

    public void setStageAndDoSetup(Stage stage) {
        this.stage = stage;

        team_name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (! newValue.matches("[\\w\\d(\\-_^*')]+")) {
                    team_name.setEffect(new InnerShadow(1000, Color.DARKRED));
                    System.out.println("BAD VALUE!");
                    feedback_team_name.setText("Plz match this -> [\\w\\d(\\-_^*')]+");
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
        address.setText("127.0.0.1");
        port.setText("7777");
        file_path.setText("pong-challenge/src/main/java/pong_sample/PongPaddle.java");
    }

    @FXML
    private void browseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your AI file");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null)
            file_path.setText(file.getAbsolutePath());
    }

    @FXML
    private void sendCodeClicked() {
        setupConnection();
        String s = null;
        try {
            s = new String(Files.readAllBytes(new File(file_path.getText()).toPath()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println();

        connection.sendMessage("RecieveModule\0" + team_name.getText() + "\0" + s);
    }

    @FXML
    private void downloadSourcesClicked() {
        setupConnection();
        connection.recieveSources();
        try {
            unzipJar("compiled", "source.jar");
        } catch (IOException e) {
            throw new RuntimeException("Unable to unzip jar. Check paths please. call for Tejp!");
        }
    }

    @FXML
    private void testMyAIClicked() {
        File file = new File(file_path.getText());
        String s = null;
        Object instanceObj = null;
        try {
            s = new String(Files.readAllBytes(file.toPath()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            instanceObj = JavaSourceFromString.compile(s, file.getName(), "pong_sample");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (instanceObj == null) {
            throw new IllegalArgumentException("instanceObj is null");
        }
        GameMechanic<?,?> instance = (GameMechanic<?,?>)instanceObj;
        Stage stage = new Stage();
        AITestScene testScene = new AITestScene(instance);
        stage.setScene(testScene);
        stage.show();
        testScene.play();
    }

    private void setupConnection() {
        if ( connection == null)
            connection = new Connection( address.getText(), Integer.parseInt(port.getText()) );
    }

    public File getFileToSend() {
        return new File("TempFanceFile");
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
