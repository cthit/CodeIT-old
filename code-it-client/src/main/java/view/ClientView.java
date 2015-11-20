package view;

import client.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.ClientConnection;

public class ClientView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("CodeIT");
        Scene scene = new Scene(root, 800, 825);
        scene.getStylesheets().add(
                getClass().getResource("main.css").toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.show();
        ((ClientController) loader.getController()).setStageAndDoSetup(primaryStage);
    }


    public static void main(String[] args) {

      //  ClientConnection con = new ClientConnection("10.0.0.237", 7777);
      //  con.connect();


        launch(args);
    }
}
