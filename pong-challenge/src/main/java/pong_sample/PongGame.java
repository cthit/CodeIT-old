package pong_sample;

import it.chalmers.digit.codeit.api.Competitor;
import it.chalmers.digit.codeit.api.Game;
import it.chalmers.digit.codeit.api.GameMechanic;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejp on 01/11/14.
 */
public class PongGame implements Game<PongGame, PongMove> {

    private final Competitor<PongGame, PongMove> leftCompetitor, rightCompetitor;
    private Rectangle leftPaddle = new Rectangle(5, 100, 10, 40);
    private Rectangle rightPaddle = new Rectangle(385, 100, 10, 40);
    private final double scoreArr[] = new double[2];
    private int roundsLeft;
    private final int width, height;
    private final Ball ball = new Ball(
            new Point2D.Double(200, 100),
            getRandomStartVector(),
            1
    );

    private List<Shape> gameElements = new ArrayList<>();

    public PongGame(Competitor<PongGame, PongMove> leftCompetitor, Competitor<PongGame, PongMove> rightCompetitor) {
        this(leftCompetitor, rightCompetitor, 5, 400, 200);
        leftPaddle.setFill(Color.GREEN);
        rightPaddle.setFill(Color.BLUE);
    }

    public PongGame(Competitor<PongGame, PongMove> leftCompetitor, Competitor<PongGame, PongMove> rightCompetitor, int roundsLeft, int width, int height) {
        this.leftCompetitor = leftCompetitor;
        this.rightCompetitor = rightCompetitor;
        this.roundsLeft = roundsLeft;
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
        Rectangle r = paddleLogic == leftCompetitor.getGameMechanic() ? leftPaddle : rightPaddle;
        return new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public Ball getBall() {
        return new Ball(ball);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void initializeGame() {
        roundsLeft--;
        double middleY = height/2;
        leftPaddle.setY(middleY);
        rightPaddle.setY(middleY);

        ball.setCenterY(middleY);
        ball.setCenterX(width / 2);
        ball.setDirection(getRandomStartVector());
        ball.setSpeed(1);
    }

    private Vector2D getRandomStartVector() {
        double factorX = Math.random() < 0.5 ? 1 : -1;
        double factorY = Math.random() < 0.5 ? 1 : -1;
        double velX = (Math.random()/2 + 0.5)*factorX;
        double velY = (Math.random()/2 + 0.5)*factorY;
        return new Vector2D(velX, velY);
    }

    @Override
    public void play() {
        double leftX = leftPaddle.getX() + leftPaddle.getWidth();
        double rightX = rightPaddle.getX();
        Line leftPaddleLine = new Line(leftX, leftPaddle.getY(), leftX, leftPaddle.getY() + leftPaddle.getHeight());
        Line rightPaddleLine = new Line(rightX, rightPaddle.getY(), rightX, rightPaddle.getY() + leftPaddle.getHeight());

        Line ballLine = new Line(
                ball.getCenterX(),
                ball.getCenterY(),
                ball.getCenterX() + ball.getSpeed()* ball.getDirection().getX(),
                ball.getCenterY() + ball.getSpeed()* ball.getDirection().getY());

        boolean leftHit = leftPaddle.getBoundsInParent().intersects(ballLine.getBoundsInParent());
        boolean rightHit = rightPaddle.getBoundsInParent().intersects(ballLine.getBoundsInParent());

        if (leftHit || rightHit) {
            handlePaddleHit(leftPaddleLine, rightPaddleLine, leftHit);
        } else {
            handleBallHitWall();
            handleScore();
        }

        ball.move();
        movePaddle(leftCompetitor.getGameMechanic(), leftPaddle);
        movePaddle(rightCompetitor.getGameMechanic(), rightPaddle);
    }

    private void handleScore() {
        if (ball.getCenterX() < 0) {
            scoreArr[1]++;
            initializeGame();
        } else if (width < ball.getCenterX()) {
            scoreArr[0]++;
            initializeGame();
        }
    }

    private void handleBallHitWall() {
        if (ball.getCenterY() < 0 || ball.getCenterY() > height) {
            ball.getDirection().setY(-ball.getDirection().getY());
        }
    }

    private void handlePaddleHit(Line leftPaddleLine, Line rightPaddleLine, boolean leftHit) {
        Line paddleLine = rightPaddleLine;
        int degreesToAdd = 0;

        if (leftHit) {
            paddleLine = leftPaddleLine;
            degreesToAdd = 180;
        }

        double offsetY = paddleLine.getStartY();

        double paddleBot = paddleLine.getEndY() - offsetY;

        final double ballCoordWithinPaddle = ball.getCenterY() - offsetY;

        //should be a float number between -1 - 1
        double normalizedBallPosOnPaddle = (ballCoordWithinPaddle / paddleBot) * 2 - 1;
        if (Math.abs(normalizedBallPosOnPaddle) > 1) {
            // Print error if not between -1 and 1
            //System.err.println("should be between -1 and 1: " + normalizedBallPosOnPaddle);
        }

        double degreeOnUnitCircle = normalizedBallPosOnPaddle * 70 + degreesToAdd;

        Vector2D vectorToAdd = new Vector2D(Math.cos(degreeOnUnitCircle),Math.sin(degreeOnUnitCircle));
        ball.setDirection(new Vector2D(-ball.getDirection().getX(), ball.getDirection().getY()));
        //ball.getDirection().add(vectorToAdd).normalize();

        ball.setSpeed(ball.getSpeed() + 0.01);
    }

    private void movePaddle(GameMechanic<PongGame, PongMove> paddleLogic, Rectangle paddle) {
        int direction = paddleLogic.onGameTick(this).getDirection();
        paddle.setY(paddle.getY() + (double)direction * 0.4);

        if (paddle.getY() < 0)
            paddle.setY(0);
        else if(paddle.getY() + paddle.getHeight() > height)
            paddle.setY(height - paddle.getHeight());

    }

    @Override
    public boolean isGameOver() {
        return roundsLeft < 0;
    }

    @Override
    public List<Shape> getScreenElements() {
        return gameElements;
    }

    @Override
    public double[] getResults() {
        return scoreArr.clone();
    }

    public static class Ball extends Circle {
        private Vector2D direction;
        private double speed;

        public Ball(Point2D.Double pos, Vector2D direction, double speed) {
            super(pos.x, pos.y, 3, Color.BLACK);
            this.direction = direction;
            this.speed = speed;
        }

        public Ball(Ball ball) {
            super(ball.getCenterX(), ball.getCenterY(), ball.getRadius(), ball.getFill());
            this.speed = ball.speed;
            this.direction = new Vector2D(ball.direction);
        }

        public void move() {
            setCenterX(getCenterX() + direction.getX() * speed);
            setCenterY(getCenterY() + direction.getY() * speed);
        }

        public double getSpeed() {
            return speed;
        }

        public Vector2D getDirection() {
            return direction;
        }

        public void setDirection(Vector2D direction) {
            this.direction = direction;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }
    }
}
