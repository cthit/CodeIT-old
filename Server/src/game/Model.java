package game;

import game.pong_sample.PongGame;
import game.pong_sample.PongMove;
import game.pong_sample.PongPaddle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by tejp on 01/11/14.
 */
public class Model<T, M> {
    private final BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory;
    private final List<Competitor<T, M>> competitors = new ArrayList<>();

    public boolean addCompetitor(Competitor competitor) {
        return competitors.add(competitor);
    }

    public boolean removeCompetitor(Competitor competitor) {
        return competitors.remove(competitor);
    }

    public Model(BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory) {
        this.gameFactory = gameFactory;
    }

    public void play() {
        for (Competitor<T, M> player1 : competitors) {
            for (Competitor<T, M> player2 : competitors) {
                if (player1 != player2) {
                    Game game = gameFactory.apply(player1, player2);
                    while (!game.isGameOver()) {
                        game.play();
                    }
                    System.out.println("Rating: " + game.getWinner() + " " + game.getRating(game.getWinner()));

                }
            }
        }
    }

    public static void main(String[] args) {
        Model<PongGame, PongMove> model = new Model((a, b) -> new PongGame(((Competitor<PongGame, PongMove>)a).getGameMechanic(), ((Competitor<PongGame, PongMove>)b).getGameMechanic()));
        Competitor<PongGame, PongMove> competitor1 = new Competitor("Team1", new PongPaddle());
        Competitor<PongGame, PongMove> competitor2 = new Competitor("Team1", new PongPaddle());
        System.out.println("1: " + competitor1);
        System.out.println("2: " + competitor2);
        model.addCompetitor(competitor1);
        model.addCompetitor(competitor2);
        model.play();
    }
}
