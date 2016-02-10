package it.chalmers.digit.codeit.api;

/**
 * Created by tejp on 01/11/14.
 */
public class Competitor<T, M> {

    private final String teamName;
    private double rating = 1500;
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

    public double getRating() {
        return rating;
    }

    public void addRating(double score) {
        this.rating += score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Competitor that = (Competitor) o;

        if (Double.compare(that.rating, this.rating) != 0) return false;
        if (this.gameMechanic != null ? !this.gameMechanic.equals(that.gameMechanic) : that.gameMechanic != null) return false;
        if (this.teamName != null ? !this.teamName.equals(that.teamName) : that.teamName != null) return false;

        return true;
    }
}
