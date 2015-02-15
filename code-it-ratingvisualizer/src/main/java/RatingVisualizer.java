import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RatingVisualizer extends Application {

    private final TableView table = new TableView();
    private final ObservableList<TeamRatingEntry> data = FXCollections.observableArrayList();

    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(300);
        stage.setHeight(500);

        final Label label = new Label("RatingTable");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);

        TableColumn teamNameColumn = new TableColumn("Team");
        teamNameColumn.setCellValueFactory(
                new PropertyValueFactory<TeamRatingEntry, String>("teamName"));
        TableColumn ratingColumn = new TableColumn("Rating");
        ratingColumn.setCellValueFactory(
                new PropertyValueFactory<TeamRatingEntry, String>("rating"));


        table.setItems(data);
        table.getColumns().addAll(teamNameColumn, ratingColumn);


        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();



        scheduledExecutor.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                data.clear();
                Map<String, Double> ratings = requestRatingFromServer();
                ratings.entrySet().forEach(e -> data.add(new TeamRatingEntry(
                        e.getKey(),
                        e.getValue()
                )));
            });

        }, 0, 5, TimeUnit.SECONDS);
    }

    public Map<String, Double> requestRatingFromServer() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("10.0.0.231");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = 7777;


        Socket socket = null;
        try {
            socket = new Socket(inetAddress, port);
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] byteMessage = "RequestRating".getBytes();
            for (int i = 0 ; i < byteMessage.length ; i++) {
                bos.write(byteMessage[i]);
            }
            bos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //**************************************************************************************************************


        BufferedInputStream bis;
        ObjectInputStream ois;
        Map<String,Double> receivedRatings = null;
        try {
            bis = new BufferedInputStream(socket.getInputStream());
            ois = new ObjectInputStream(bis);

            receivedRatings =  (Map<String, Double>)ois.readObject();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return receivedRatings;
    }

    public static class TeamRatingEntry {
        private final SimpleStringProperty teamName;
        private final SimpleDoubleProperty rating;

        public TeamRatingEntry(String teamName, double rating) {
            this.teamName = new SimpleStringProperty(teamName);
            this.rating = new SimpleDoubleProperty(rating);
        }

        public String getTeamName() {
            return teamName.get();
        }

        public void setTeamName(String teamName) {
            this.teamName.set(teamName);
        }

        public double getRating() {
            return rating.get();
        }


        public void setRating(double rating) {
            this.rating.set(rating);
        }
    }
}