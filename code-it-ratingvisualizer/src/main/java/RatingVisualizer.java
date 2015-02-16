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

//        loader.getController()

        final Label label = new Label("RatingTable");
        label.setFont(new Font("Arial", 20));

        ((RatingVisualizerController)loader.getController()).startDataWatcher();
//        table.setItems(data );
//        table.getColumns().addAll(teamNameColumn, ratingColumn);

        stage.setScene(scene);
        stage.show();




    }




}