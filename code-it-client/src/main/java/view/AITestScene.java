package view;

import it.tejp.codeit.api.Game;
import it.tejp.codeit.api.GameMechanic;
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

    public AITestScene(GameMechanic<?,?> gameMechanic) {
        super(new Group());
        game = (Game<?, ?>) gameMechanic.createTestGame();

        ((Group)getRoot()).getChildren().addAll(game.getScreenElements());

        // TODO starta nytt spel. släng in två nya instanser av testarens ai. hämta gameelements börja spelet
    }

    public void play() {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(0.1), e -> game.play()));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play(); // Start animation
    }

}
