package pong_sample;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.GameMechanic;
import javafx.scene.shape.Rectangle;

/**
 * Created by tejp on 01/11/14.
 */
public class SimplePongPaddle extends AbstractPongPaddle {

    @Override
    public PongMove onGameTick(PongGame game) {
        Rectangle paddle = game.getPaddle(this);

        final double paddleLocation = paddle.getY() + paddle.getHeight() / 2;
        final double ballPos = game.getBall().getCenterY();
        return paddleLocation > ballPos ? PongMove.DOWN : PongMove.UP;
    }
}