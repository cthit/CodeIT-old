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
import org.controlsfx.dialog.Dialogs;
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
    @FXML private TextField simulation_delay;
    @FXML private Label feedback_team_name;
    @FXML private Label feedback_connection;
    @FXML private Label feedback_project_path;
    @FXML private Label feedback_simulation;
    private Connection connection;


    public void setStageAndDoSetup(Stage stage) {
        this.stage = stage;

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

        address.textProperty().addListener(listener);
        simulation_delay.textProperty().addListener(numberOnly);

        team_name.setText("qwerty");
        address.setText("127.0.0.1");
        port.setText("7777");
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
        setupConnection();
        String code = null;
        try {
            code = new String(Files.readAllBytes(new File(file_path.getText()).toPath()));

        } catch (IOException e) {
            Dialogs.create()
                    .owner(stage)
                    .title("File error")
                    .masthead("Couldn't read file")
                    .message(("Path: " + file_path.getText()))
                    .showError();
            return;
        }
        try {
            connection.sendMessage("RecieveModule\0" + team_name.getText() + "\0" + code);
        } catch (RuntimeException e) {
            Dialogs.create()
                    .owner(stage)
                    .title("Network error")
                    .masthead(e.getMessage())
                    .message("Make sure the IP address is correct: " + address.getText())
                    .showError();
        }
    }

    @FXML
    private void downloadSourcesClicked() {
        setupConnection();
        try {
            connection.recieveSources();
        } catch(RuntimeException e) {
            Dialogs.create()
                    .owner(stage)
                    .title("Network error")
                    .masthead(e.getMessage())
                    .message("Couldn't download source.jar make sure the IP address is correct: " + address.getText())
                    .showError();
            return;
        }
        try {
            new File("compiled").mkdir();
            unzipJar("compiled", "source.jar");
        } catch (IOException e) {
            Dialogs.create()
                    .owner(stage)
                    .title("File error")
                    .masthead("Couldn't unzip jarfile")
                    .message("Path: compiled/source.jar")
                    .showError();
        }
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
            Dialogs.create()
                    .owner(stage)
                    .title("Number error")
                    .masthead("Not a number")
                    .message(e.getMessage())
                    .showWarning();
            return;
        }
        try {
            code = new String(Files.readAllBytes(file.toPath()));

        } catch (IOException e) {
            Dialogs.create()
                    .owner(stage)
                    .title("File error")
                    .masthead("Couldn't read file")
                    .message(("Path: " + file_path.getText()))
                    .showError();
            return;
        }
        code = code.replaceFirst("package\\s+.+?;", "package pong_sample;");
        try {
            instanceObj = JavaSourceFromString.compile(code, file.getName(), "pong_sample");
        } catch (RuntimeException e) {
            Dialogs.create()
                    .owner(stage)
                    .title("Compiler error")
                    .masthead("Couldn't compile class")
                    .message("Error: " + e.getMessage())
                    .showError();
            return;
        }
        if (instanceObj == null) {
            Dialogs.create()
                    .owner(stage)
                    .title("Compiler error")
                    .masthead("Couldn't instantiate class")
                    .message(("instanceObj == null"))
                    .showError();
            return;
        }
        GameMechanic<?,?> instance = (GameMechanic<?,?>)instanceObj;
        Stage stage = new Stage();
        AITestScene testScene = new AITestScene(instance, delay);
        stage.setScene(testScene);
        stage.show();
        testScene.play();
    }

    private void setupConnection() {
        if (connection == null || !connection.getInetAddress().equals(address.getText()))
            connection = new Connection( address.getText(), Integer.parseInt(port.getText()) );
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
