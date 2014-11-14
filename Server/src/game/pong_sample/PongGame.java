package game.pong_sample;

import game.Game;
import game.GameMechanic;

import java.awt.geom.Point2D;

/**
 * Created by tejp on 01/11/14.
 */
public class PongGame implements Game {

    private final GameMechanic<PongGame, PongMove> leftPaddleLogic, rightPaddleLogic;
    private Paddle leftPaddle = new Paddle(new Point2D.Double(200, 100), 40), rightPaddle = new Paddle(new Point2D.Double(200, 100), 40);
    private final int width, height;
    private final Ball ball = new Ball(200, 100);


    public PongGame(GameMechanic<PongGame, PongMove> leftPaddleLogic, GameMechanic<PongGame, PongMove> rightPaddleLogic) {
        this(leftPaddleLogic, rightPaddleLogic, 400, 200);
    }

    public PongGame(GameMechanic<PongGame, PongMove> leftPaddleLogic, GameMechanic<PongGame, PongMove> rightPaddleLogic, int width, int height) {
        this.leftPaddleLogic = leftPaddleLogic;
        this.rightPaddleLogic = rightPaddleLogic;
        this.width = width;
        this.height = height;
    }

    @Override
    public void play() {
        ball.move();
        movePaddle(leftPaddleLogic, leftPaddle);
        movePaddle(rightPaddleLogic, rightPaddle);

        if (ball.pos.y < 0 || ball.pos.y > height){
            ball.velocity.y = -ball.velocity.y;
        }
    }

    private void movePaddle(GameMechanic<PongGame, PongMove> paddleLogic, Paddle paddle) {
        int direction = paddleLogic.onGameTick(this).getDirection();
        paddle.moveY(direction);
    }

    @Override
    public boolean isGameOver() {
        return ball.pos.x < 0 || ball.pos.x > width;
    }

    @Override
    public GameMechanic<?, ?> getWinner() {
        if (ball.pos.x < 0){
            return leftPaddleLogic;
        } else if (ball.pos.x > width)
            return rightPaddleLogic;
        return null;
    }

    @Override
    public int getRating(GameMechanic<?, ?> who) {
        return who == getWinner() ? 1 : 0;
    }

    private static class Ball {
        Point2D.Double pos;
        Point2D.Double velocity;

        public Ball(Point2D.Double pos, Point2D.Double velocity) {
            this.pos = pos;
            this.velocity = velocity;
        }

        public Ball(double x, double y) {
            this(new Point2D.Double(x, y), new Point2D.Double(Math.random()*2 - 1, Math.random()*2 - 1 ));
        }

        public void move() {
            pos.x += velocity.x;
            pos.y += velocity.y;
        }

    }

    private static class Paddle {
        //middle of paddle
        Point2D.Double pos;
        double length;

        public Paddle(Point2D.Double pos, double length) {
            this.pos = pos;
            this.length = length;
        }

        public void moveY(double offset) {
            pos.y += offset;
        }
    }
}
