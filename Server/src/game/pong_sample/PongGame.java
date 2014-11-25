package game.pong_sample;

import game.Game;
import game.GameMechanic;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejp on 01/11/14.
 */
public class PongGame implements Game {

    private final GameMechanic<PongGame, PongMove> leftPaddleLogic, rightPaddleLogic;
    private Rectangle leftPaddle = new Rectangle(10, 40, Color.BLUE);
    private Rectangle rightPaddle = new Rectangle(10, 40, Color.GREEN);
    private final int width, height;
    private final Ball ball = new Ball(200, 100);

    List<Shape> gameElements = new ArrayList<>();

    public PongGame(GameMechanic<PongGame, PongMove> leftPaddleLogic, GameMechanic<PongGame, PongMove> rightPaddleLogic) {
        this(leftPaddleLogic, rightPaddleLogic, 400, 200);
    }

    public PongGame(GameMechanic<PongGame, PongMove> leftPaddleLogic, GameMechanic<PongGame, PongMove> rightPaddleLogic, int width, int height) {
        System.out.println("Play!*");
        this.leftPaddleLogic = leftPaddleLogic;
        this.rightPaddleLogic = rightPaddleLogic;
        this.width = width;
        this.height = height;

        gameElements.add(leftPaddle);
        gameElements.add(rightPaddle);
    }

    @Override
    public void play() {
        ball.move();
        movePaddle(leftPaddleLogic, leftPaddle);
        movePaddle(rightPaddleLogic, rightPaddle);

        if (ball.getCenterY() < 0 || ball.getCenterY() > height){
            ball.velocity.y = -ball.velocity.y;
        }
    }

    private void movePaddle(GameMechanic<PongGame, PongMove> paddleLogic, Rectangle paddle) {
        int direction = paddleLogic.onGameTick(this).getDirection();
        paddle.setY(paddle.getY() + direction);
    }

    @Override
    public boolean isGameOver() {
        return ball.getCenterX() < 0 || ball.getCenterX() > width;
    }

    @Override
    public GameMechanic<?, ?> getWinner() {
        if (ball.getCenterX() < 0){
            return leftPaddleLogic;
        } else if (ball.getCenterX() > width)
            return rightPaddleLogic;
        return null;
    }

    @Override
    public int getRating(GameMechanic<?, ?> who) {
        return who == getWinner() ? 1 : 0;
    }

    @Override
    public List<Shape> getScreenElements() {
        return gameElements;
    }

    private static class Ball extends Circle {
        private Point2D.Double velocity;

        public Ball(Point2D.Double pos, Point2D.Double velocity) {
            super(pos.x, pos.y, 3, Color.BLACK);
            this.velocity = velocity;
        }

        public Ball(double x, double y) {
            this(new Point2D.Double(x, y), new Point2D.Double(Math.random()*2 - 1, Math.random()*2 - 1 ));
        }

        public void move() {
            setCenterX(getCenterX() + velocity.x);
            setCenterY(getCenterY() + velocity.y);
        }

    }
}
