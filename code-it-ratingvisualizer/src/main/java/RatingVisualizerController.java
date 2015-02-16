import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by tejp on 16/02/15.
 */
public class RatingVisualizerController {


    private ObservableList<TeamRatingEntry> data = FXCollections.observableArrayList();

    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    @FXML private TableView table;

    public void startDataWatcher() {
        System.out.println(table.toString());

        data = table.getItems();

        scheduledExecutor.scheduleAtFixedRate(() -> {
            final ObservableList items = table.getItems();

            Platform.runLater(() -> {
                items.clear();
                Map<String, Double> ratings = requestRatingFromServer();
                ratings.entrySet().forEach(e -> {
                    items.add(new TeamRatingEntry(
                            e.getKey(),
                            e.getValue()
                    ));
                });
            });

        }, 0, 5, TimeUnit.SECONDS);



    }

    public Map<String, Double> requestRatingFromServer() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("127.0.0.1");
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
