package game;

/**
 * Created by tejp on 01/11/14.
 */
public interface Game {
    void play();
    boolean isGameOver();
    GameMechanic<?, ?> getWinner();
    int getRating(GameMechanic<?, ?> who);
}
