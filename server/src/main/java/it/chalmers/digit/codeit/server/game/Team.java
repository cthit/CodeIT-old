package it.chalmers.digit.codeit.server.game;

import it.chalmers.digit.codeit.api.GameMechanic;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by tejp on 20/12/15.
 */
public class Team<T,M> {

    private String teamName;
    private GameMechanic<T,M> mechanic;


    public Team(String teamName) {
        this.teamName = teamName;
    }
    public String getTeamName() {
        return teamName;
    }

    public void setMechanic(GameMechanic<T, M> mechanic) {
        this.mechanic = mechanic;
    }

    public GameMechanic<T, M> getMechanic() {
        return mechanic;
    }
}
