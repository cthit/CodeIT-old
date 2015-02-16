package it.tejp.codeit.api;

import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Created by tejp on 01/11/14.
 */
public interface Game<T, M> {
    void play();
    boolean isGameOver();
    List<Shape> getScreenElements();
    double[] getResults();
}
