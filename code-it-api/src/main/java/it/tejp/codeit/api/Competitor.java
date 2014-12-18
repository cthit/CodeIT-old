package it.tejp.codeit.api;

/**
 * Created by tejp on 01/11/14.
 */
public class Competitor<T, M> {

    private final String teamName;
    private final GameMechanic<T, M> gameMechanic;

    public Competitor(String teamName, GameMechanic<T, M> gameMechanic) {
        this.teamName = teamName;
        this.gameMechanic = gameMechanic;
    }

    public GameMechanic<T, M> getGameMechanic() {
        return gameMechanic;
    }

    public String getTeamName() {
        return teamName;
    }
}
