package pong_sample;


import it.tejp.codeit.api.Move;

/**
 * Created by tejp on 01/11/14.
 */
public enum PongMove implements Move<PongMove> {
    UP(1), DOWN(-1), NONE(0);

    private int direction;
    PongMove(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }
}
