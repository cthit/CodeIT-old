package game;

/**
 * Created by tejp on 01/11/14.
 */
public interface GameMechanic<T, M> {
    M onGameTick(T game);
}
