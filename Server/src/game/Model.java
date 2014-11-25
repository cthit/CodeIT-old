package game;

import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import game.pong_sample.PongGame;
import game.pong_sample.PongMove;
import game.view.NewGameListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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

    public void createNewGame(Competitor<T,M> competitor1, Competitor<T,M> competitor2) {
        game = gameFactory.apply(competitor1, competitor2);
    }

    public void gameLoop() {
        if ( ! game.isGameOver())
            game.play();
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


    public static class Round<T,M> {
        private final Competitor<T,M> competitor1;
        private final Competitor<T,M> competitor2;

        public Round(Competitor<T,M> competitor1, Competitor<T,M> competitor2) {
            this.competitor1 = competitor1;
            this.competitor2 = competitor2;
        }


    }
    public static class RoundIterator<T,M> implements Iterator<Round> {

        private List<Competitor<T,M>> competitors = new ArrayList<>();
        private int competitor1 = 0;
        private int competitor2 = 1;

        public RoundIterator(List<Competitor<T,M>> competitors) {
            this.competitors = competitors;
        }

        @Override
        public boolean hasNext() {
            return competitor1 < competitors.size() - 1 ;
        }

        @Override
        public Round next() {
            Round round = new Round(competitors.get(competitor1), competitors.get(competitor2));

            if (competitor2 >= competitors.size() - 1) {
                competitor1++;
                competitor2 = competitor1;
            }
            competitor2++;
            return round;
        }

        @Override
        public void remove() {
            throw new ActionNotSupportedException("Can't remove Round from iterator");
        }

        @Override
        public void forEachRemaining(Consumer<? super Round> action) {

        }
    }
}
