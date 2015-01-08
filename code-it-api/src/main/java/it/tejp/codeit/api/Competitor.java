package it.tejp.codeit.api;

/**
 * Created by tejp on 01/11/14.
 */
public class Competitor<T, M> {

    private final String teamName;
    private double score;
    private GameMechanic<T, M> gameMechanic;

    public Competitor(String teamName, GameMechanic<T, M> gameMechanic) {
        this.teamName = teamName;
        this.gameMechanic = gameMechanic;
    }

    public GameMechanic<T, M> getGameMechanic() {
        return gameMechanic;
    }

    public void setGameMechanic(GameMechanic<T, M> gameMechanic) {
        this.gameMechanic = gameMechanic;
    }

    public String getTeamName() {
        return teamName;
    }

    public double getScore() {
        return score;
    }

    public void addScore(double score) {
        this.score += score;
    }
}
