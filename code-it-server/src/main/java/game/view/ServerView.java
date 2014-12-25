package game.view;

import game.Model;
import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import pong_sample.PongGame;
import pong_sample.PongMove;
import pong_sample.PongPaddle;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by tejp on 14/11/14.
 */
public class ServerView extends Application implements NewGameListener {

    private Stage primaryStage;
    Pane canvas = new Pane();
    private Game game;
    private List<Shape> screenElements;

    public void setGame(Game game) {
        this.game = game;
        canvas.getChildren().clear();
        canvas.getChildren().addAll(game.getScreenElements());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Competitor<PongGame, PongMove> competitor1 = new Competitor("Team1", new PongPaddle());
        Competitor<PongGame, PongMove> competitor2 = new Competitor("Team2", new PongPaddle());

        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a,b) -> new PongGame(a, b);

        Model<PongGame, PongMove> model = new Model(100, gameFactory, competitor1, competitor2);
        model.setNewGameListener(this);

        primaryStage.setTitle("it.tejp.codeit.api.Game");

        Group root = new Group();

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Model.CompetitorPairIterator pairIterator = model.getCompetitorPairIterator();

        game = model.createNewGame(pairIterator.next());

        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), t -> {

            if (game.isGameOver()) {
                model.storeRating(game);

                if (pairIterator.hasNext()) {
                    game = model.createNewGame(pairIterator.next());
                }
            } else {
                game.play();
                //TODO paint stuff?

            }

        }));
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
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

    public static void main(String[] args) {
        launch();
    }
}
