package it.chalmers.digit.codeit.client.view;

import it.chalmers.digit.codeit.api.Game;
import it.chalmers.digit.codeit.api.GameMechanic;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.List;

/**
 * Created by tejp on 06/01/15.
 */
public class AITestScene extends Scene {

    private final List<Shape> gameElements = null;
    private final Game<?,?> game;
    private final double delay;

    public AITestScene(GameMechanic<?,?> gameMechanic, double delay) {
        super(new Group());
        this.delay = delay;
        game = (Game<?, ?>) gameMechanic.createTestGame();

        ((Group)getRoot()).getChildren().addAll(game.getScreenElements());

        // TODO starta nytt spel. släng in två nya instanser av testarens ai. hämta gameelements börja spelet
    }

    public void play() {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(delay), e -> game.play()));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play(); // Start animation
    }

}
