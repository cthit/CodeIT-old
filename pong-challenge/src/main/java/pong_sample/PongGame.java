package pong_sample;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import it.tejp.codeit.api.GameMechanic;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by tejp on 01/11/14.
 */
public class PongGame implements Game<PongGame, PongMove> {

    private final Competitor<PongGame, PongMove> leftCompetitor, rightCompetitor;
    private Rectangle leftPaddle = new Rectangle(0, 100, 10, 40);
    private Rectangle rightPaddle = new Rectangle(400, 100, 10, 40);
    private Double leftPaddleScore = new Double(0);
    private Double rightPaddleScore = new Double(0);
    private final int width, height;
    private final Ball ball = new Ball(200, 100);

    private List<Shape> gameElements = new ArrayList<>();

    public PongGame(Competitor<PongGame, PongMove> leftCompetitor, Competitor<PongGame, PongMove> rightCompetitor) {
        this(leftCompetitor, rightCompetitor, 400, 200);
        leftPaddle.setFill(Color.GREEN);
        rightPaddle.setFill(Color.BLUE);
    }

    public PongGame(Competitor<PongGame, PongMove> leftCompetitor, Competitor<PongGame, PongMove> rightCompetitor, int width, int height) {
        System.out.println("PongGameCreated");
        this.leftCompetitor = leftCompetitor;
        this.rightCompetitor = rightCompetitor;
        this.width = width;
        this.height = height;

        gameElements.add(leftPaddle);
        gameElements.add(rightPaddle);
        gameElements.add(ball);
        gameElements.add(new Line(0, 1, width, 1));
        gameElements.add(new Line(0, height - 1, width, height - 1));
        gameElements.add(new Line(1, 0, 1, height));
        gameElements.add(new Line(width, 0, width, height));
    }

    public Rectangle getPaddle(GameMechanic<PongGame, PongMove> paddleLogic) {
        return paddleLogic == leftCompetitor.getGameMechanic() ? leftPaddle : rightPaddle;
    }

    public Ball getBall() {
        return ball;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void play() {
        ball.move();
        System.out.println();
        System.out.println(String.format("(%f;%f)", ball.getCenterX(), ball.getCenterY()));
        System.out.println(String.format("(%f,%f)", leftPaddle.getX(), leftPaddle.getY()));
        System.out.println(String.format("(%f,%f)", rightPaddle.getX(), rightPaddle.getY()));
        System.out.println();
        movePaddle(leftCompetitor.getGameMechanic(), leftPaddle);
        movePaddle(rightCompetitor.getGameMechanic(), rightPaddle);

        if (ball.getCenterY() < 0 || ball.getCenterY() > height) {
            ball.velocity.y = -ball.velocity.y;
        }

        if (leftPaddle.intersects(ball.boundsInLocalProperty().get()) || rightPaddle.intersects(ball.boundsInLocalProperty().get())) {
            ball.getVelocity().x = -ball.getVelocity().x;
        }
    }

    private void movePaddle(GameMechanic<PongGame, PongMove> paddleLogic, Rectangle paddle) {
        int direction = paddleLogic.onGameTick(this).getDirection();
        paddle.setY(paddle.getY() + direction);

        if (paddle.getY() < 0)
            paddle.setY(0);
        else if(paddle.getY() + paddle.getHeight() > height)
            paddle.setY(height - paddle.getHeight());

    }

    @Override
    public boolean isGameOver() {
        return ball.getCenterX() < 0 || ball.getCenterX() > width;
    }

    @Override
    public Map<Competitor<PongGame,PongMove>, Double> getResults() {
        Map<Competitor<PongGame,PongMove>, Double> results = new HashMap<>();
        results.put(leftCompetitor, leftPaddleScore);
        results.put(rightCompetitor, rightPaddleScore);
        return results;
    }

    @Override
    public List<Shape> getScreenElements() {
        return gameElements;
    }

    public static class Ball extends Circle {
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

        public Point2D.Double getVelocity() {
            return velocity;
        }
    }
}
