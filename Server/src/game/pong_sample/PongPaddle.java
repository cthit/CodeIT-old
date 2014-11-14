package game.pong_sample;

import game.GameMechanic;

/**
 * Created by tejp on 01/11/14.
 */
public class PongPaddle implements GameMechanic<PongGame, PongMove> {

    @Override
    public PongMove onGameTick(PongGame game) {
        return PongMove.DOWN;
    }
}
