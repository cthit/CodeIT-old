package game;

import game.view.NewGameListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by tejp on 01/11/14.
 */
public class Model<T, M> {
    private final BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory;
    private final List<Competitor<T, M>> competitors = new ArrayList<>();
    private Game game;
    private NewGameListener newGameListener;

    public boolean addCompetitor(Competitor competitor) {
        return competitors.add(competitor);
    }

    public boolean removeCompetitor(Competitor competitor) {
        return competitors.remove(competitor);
    }

    public Model(BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory) {
        this.gameFactory = gameFactory;
    }

    public void setNewGameListener(NewGameListener newGameListener) {
        this.newGameListener = newGameListener;
    }

    public void play() {
        for (Competitor<T, M> player1 : competitors) {
            for (Competitor<T, M> player2 : competitors) {
                if (player1 != player2) {
                    Game game = gameFactory.apply(player1, player2);
                    newGameListener.newGameCreated(game);
                    while (!game.isGameOver()) {
                        game.play();
                    }
                    System.out.println("Rating: " + game.getWinner() + " " + game.getRating(game.getWinner()));

                }
            }
        }
    }

    public Game getGame() {
        return game;
    }
}
