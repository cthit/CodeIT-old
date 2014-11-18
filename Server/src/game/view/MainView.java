package game.view;

import game.Competitor;
import game.Game;
import game.Model;
import game.pong_sample.PongGame;
import game.pong_sample.PongMove;
import game.pong_sample.PongPaddle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by tejp on 14/11/14.
 */
public class MainView extends Application implements NewGameListener {

    private Stage primaryStage;
    Pane canvas = new Pane();
    private Game game;
    private List<Shape> screenElements;

    public void setGame(Game game) {
        this.game = game;
        canvas.getChildren().clear();
        canvas.getChildren().addAll(game.getScreenElements());
    }

    public MainView() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Model<PongGame, PongMove> model = new Model((a, b) -> new PongGame(((Competitor<PongGame, PongMove>)a).getGameMechanic(), ((Competitor<PongGame, PongMove>)b).getGameMechanic()));
        Competitor<PongGame, PongMove> competitor1 = new Competitor("Team1", new PongPaddle());
        Competitor<PongGame, PongMove> competitor2 = new Competitor("Team1", new PongPaddle());
        model.addCompetitor(competitor1);
        model.addCompetitor(competitor2);
        model.setNewGameListener(this);

        primaryStage.setTitle("Game");

        Group root = new Group();

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        new Thread(() -> model.play()).start();


        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            @Override
            public void handle(final ActionEvent t) {
//                System.out.println("When does this happen? " + new Date().getTime());
            }
        }));
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void newGameCreated(Game game) {
        this.game = game;
        this.screenElements = game.getScreenElements();
        Platform.runLater(() -> {
            canvas.getChildren().clear();
            canvas.getChildren().addAll(screenElements);
        });

    }
}
