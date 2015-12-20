package view;

import client.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
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

    @Override
    public void stop(){
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
