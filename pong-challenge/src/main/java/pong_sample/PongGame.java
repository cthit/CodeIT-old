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
    private int roundsPerGame;
    private int roundsPlayed;
    private final int width, height;
    private final Ball ball = new Ball(200, 100);

    private List<Shape> gameElements = new ArrayList<>();

    public PongGame(Competitor<PongGame, PongMove> leftCompetitor, Competitor<PongGame, PongMove> rightCompetitor) {
        this(leftCompetitor, rightCompetitor,1000,  400, 200);
        leftPaddle.setFill(Color.GREEN);
        rightPaddle.setFill(Color.BLUE);
    }

    public PongGame(Competitor<PongGame, PongMove> leftCompetitor, Competitor<PongGame, PongMove> rightCompetitor, int roundsPerGame, int width, int height) {
        System.out.println("PongGameCreated");
        this.leftCompetitor = leftCompetitor;
        this.rightCompetitor = rightCompetitor;
        this.roundsPerGame = roundsPerGame;
        this.width = width;
        this.height = height;

        initializeGame();

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

    private void initializeGame() {
        double middleY = height/2;
        leftPaddle.setY(middleY);
        rightPaddle.setY(middleY);
        ball.setCenterY(middleY);
        ball.setCenterX(width/2);

    }

    @Override
    public void play() {
        System.out.println(ball.getVelocity().x);
        double leftX = leftPaddle.getX() + leftPaddle.getWidth();
        double rightX = rightPaddle.getX();
        Line leftPaddleLine = new Line(leftX, leftPaddle.getY(), leftX, leftPaddle.getY() + leftPaddle.getHeight());
        Line rightPaddleLine = new Line(rightX, rightPaddle.getY(), rightX, rightPaddle.getY() + leftPaddle.getHeight());

        Line ballLine = new Line(ball.getCenterX(), ball.getCenterY(), ball.getCenterX() + ball.getVelocity().x, ball.getCenterY() + ball.getVelocity().y);

        if (leftPaddleLine.intersects(ballLine.boundsInLocalProperty().get()) || rightPaddleLine.intersects(ballLine.boundsInLocalProperty().get())) {
            ball.getVelocity().x = -ball.getVelocity().x*1.1; //todo Magic numbers. they mean increase speed by 10% in both x and y velocity.
            ball.getVelocity().y = ball.getVelocity().y*1.1;
        }

        //############################################################
        ball.move();
        movePaddle(leftCompetitor.getGameMechanic(), leftPaddle);
        movePaddle(rightCompetitor.getGameMechanic(), rightPaddle);

        if (ball.getCenterY() < 0 || ball.getCenterY() > height) {
            ball.velocity.y = -ball.velocity.y;
        }

        if (ball.getCenterX() < 0) {
            rightCompetitor.addScore(1);
            roundsPlayed++;
        } else if (width < ball.getCenterX()) {
            leftCompetitor.addScore(1);
            roundsPlayed++;
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