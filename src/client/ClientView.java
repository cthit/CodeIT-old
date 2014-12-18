package client;

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
        Scene scene = new Scene(root, 300, 275);
        scene.getStylesheets().add(
                getClass().getResource("main.css").toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.show();
        ((Controller) loader.getController()).setStageAndDoSetup(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
