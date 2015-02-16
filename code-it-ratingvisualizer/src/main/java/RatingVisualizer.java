import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.io.IOException;

public class RatingVisualizer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ratingvisualizer/ratingvisualizer.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Table View Sample");
        stage.setWidth(300);
        stage.setHeight(500);

        scene.getStylesheets().addAll(this.getClass().getResource("ratingvisualizer/main.css").toExternalForm());

        final Label label = new Label("RatingTable");
        label.setFont(new Font("Arial", 20));


        int port = 7777;
        String adress = "127.0.0.1";
        switch (getParameters().getRaw().size()) {
            case 2:
                port = Integer.parseInt(getParameters().getRaw().get(1));
            case 1:
                adress = getParameters().getRaw().get(0);
        }


        final RatingVisualizerController controller = (RatingVisualizerController) loader.getController();
        controller.initialize(adress, port);
        controller.startDataWatcher();



        stage.setScene(scene);
        stage.show();




    }




}