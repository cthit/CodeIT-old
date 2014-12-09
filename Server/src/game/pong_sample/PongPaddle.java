package game.pong_sample;

import game.GameMechanic;
import javafx.scene.shape.Rectangle;

/**
 * Created by tejp on 01/11/14.
 */
public class PongPaddle implements GameMechanic<PongGame, PongMove> {

    boolean yPosFound = false;
    int yPos = 0;

    @Override
    public PongMove onGameTick(PongGame game) {

        if (! yPosFound) {
            yPos = 42;
            yPosFound = true;
        }
        /*
        PongGame.Ball ball = game.getBall();

        double ticsToWall = ball.getCenterX() / ball.getVelocity().x;

        double yHitPos = ball.getVelocity().y * ticsToWall;

        yHitPos %= game.getHeight();
        System.out.println("yhitpos: " + yHitPos);

        */

//        return yHitPos < game.getLeftPaddle().getX() ? PongMove.UP : PongMove.DOWN;

        Rectangle paddle = game.getPaddle(this);

        return getDirectionToGo(paddle.getX() + paddle.getHeight() / 2, yPos);
    }

    PongMove getDirectionToGo(double paddleLocation, double goalPos) {
        if (paddleLocation > goalPos)
            return PongMove.DOWN;
        return PongMove.UP;
    }
}
