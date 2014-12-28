package game;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.util.function.BiFunction;

/**
 * Created by tejp on 28/12/14.
 */
public class ServerController {

    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

    public void start() {
        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a, b) -> new PongGame(a, b);

        Model<PongGame, PongMove> model = new Model(100, gameFactory, competitor1, competitor2);


        Model.CompetitorPairIterator pairIterator = model.getCompetitorPairIterator();

        game = model.createNewGame(pairIterator.next());

        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), t -> {

            if (game.isGameOver()) {
                game.storeRating();

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

}
