package game.pong_sample;

import game.GameMechanic;
import javafx.scene.shape.Rectangle;

/**
 * Created by tejp on 01/11/14.
 */
public class PongPaddle implements GameMechanic<PongGame, PongMove> {

    @Override
    public PongMove onGameTick(PongGame game) {

        Rectangle paddle = game.getPaddle(this);

        return getDirectionToGo(paddle.getY() + paddle.getHeight() / 2, game.getBall().getCenterY());
    }

    PongMove getDirectionToGo(double paddleLocation, double goalPos) {
        if (paddleLocation > goalPos)
            return PongMove.DOWN;
        return PongMove.UP;
    }
}
