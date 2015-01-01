package game;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import network.ServerConnection;
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.util.function.BiFunction;

/**
 * Created by tejp on 28/12/14.
 */
public class ServerController {

    private ServerConnection connection;
    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

    public void start() {
        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a, b) -> new PongGame(a, b);

        Model<PongGame, PongMove> model = new Model(100, gameFactory);


        Model.CompetitorPairIterator pairIterator = model.getCompetitorPairIterator();

        game = model.createNewGame(pairIterator.next());

        while (true) {
            if (game.isGameOver()) {
                if (pairIterator.hasNext()) {
                    game = model.createNewGame(pairIterator.next());
                }
            } else {
                game.play();
            }

        }
    }

}
