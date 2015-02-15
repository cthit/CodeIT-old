package game;

import it.tejp.codeit.api.Competitor;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;
import pong_sample.AbstractPongPaddle;
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ModelTest {

    Model<PongGame,PongMove> model;
    List<Competitor<PongGame,PongMove>> competitors = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        model = new Model<>((a, b) -> new PongGame(a, b));
        competitors.add(new Competitor<>("team1", new PongPaddle1()));
        competitors.add(new Competitor<>("team2", new PongPaddle2()));
        competitors.add(new Competitor<>("team3", new SimplePongPaddle()));
    }



    @Test
    public void testHandleContributionFromCompetitor() throws Exception {

        model.handleContributionFromCompetitor("team1", new PongPaddle1());
        model.handleContributionFromCompetitor("team2", new PongPaddle2());
        model.getCompetitors().forEach(c -> System.out.println(c.getTeamName() + ": " + c.getRating()));
        System.out.println("******************************");
        model.handleContributionFromCompetitor("teamSimplePongPaddle", new SimplePongPaddle());
        model.handleContributionFromCompetitor("teamSimplePongPaddle2", new SimplePongPaddle());
        model.handleContributionFromCompetitor("teamSimplePongPaddle3", new SimplePongPaddle());
        model.getCompetitors().forEach(c -> System.out.println(c.getTeamName() + ": " + c.getRating()));

        assertTrue(true);
    }

    @Test
    public void testEvaluateCompetitor() throws Exception {
    }

    private static class PongPaddle1 extends AbstractPongPaddle {

        @Override
        public PongMove onGameTick(PongGame game) {
            return PongMove.DOWN;
        }

    }

    private static class PongPaddle2 extends AbstractPongPaddle {

        @Override
        public PongMove onGameTick(PongGame game) {
            return Math.random() > 0.5 ? PongMove.UP : PongMove.DOWN ;
        }

    }

    public class SimplePongPaddle extends AbstractPongPaddle {
        @Override
        public PongMove onGameTick(PongGame game) {
            Rectangle paddle = game.getPaddle(this);

            final double paddleLocation = paddle.getY() + paddle.getHeight() / 2;
            final double ballPos = game.getBall().getCenterY();
            return paddleLocation > ballPos ? PongMove.DOWN : PongMove.UP;
        }

    }
}