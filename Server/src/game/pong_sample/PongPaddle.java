package game.pong_sample;

import game.GameMechanic;

import java.util.Random;

/**
 * Created by tejp on 01/11/14.
 */
public class PongPaddle implements GameMechanic<PongGame, PongMove> {

    PongMove[] pongMoves = PongMove.values();
    Random random = new Random();

    @Override
    public PongMove onGameTick(PongGame game) {
        return pongMoves[random.nextInt(pongMoves.length)];
    }
}
