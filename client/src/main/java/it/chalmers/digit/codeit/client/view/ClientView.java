package it.chalmers.digit.codeit.client.view;

import it.chalmers.digit.codeit.client.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientView extends Application {

    private final int MIN_HEGTH = 800;
    private final int MIN_WIDTH = 825;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("CodeIT");
        primaryStage.setMinHeight(MIN_HEGTH);
        primaryStage.setMinWidth(MIN_WIDTH);

        LoginController controller = loader.<LoginController>getController();
        controller.setup(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
