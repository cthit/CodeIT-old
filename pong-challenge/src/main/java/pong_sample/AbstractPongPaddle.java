package pong_sample;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.GameMechanic;

/**
 * Created by tejp on 15/02/15.
 */
public abstract class AbstractPongPaddle implements GameMechanic<PongGame, PongMove>, Cloneable{

    @Override
    public final PongGame createTestGame() {
        try {
            return new PongGame(new Competitor<>("LeftCompetitor", (GameMechanic<PongGame, PongMove>)this.clone()), new Competitor<>("RightCompetitor", this));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
