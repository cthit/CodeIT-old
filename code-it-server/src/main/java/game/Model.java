package game;

import game.view.NewGameListener;
import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Created by tejp on 01/11/14.
 */
public class Model<T, M> {
    private final BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory;
    private final Map<Competitor<T,M>, Double> competitorScoreMap = new HashMap<>();
    private final CompetitorPairIterator<T,M> competitorPairIterator;
    private Game<T, M> game;
    private NewGameListener newGameListener;
    private int roundsPerPair;

    public Model(int roundsPerPair, BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory, Competitor<T,M>... competitors) {
        this.roundsPerPair = roundsPerPair;
        this.gameFactory = gameFactory;

        competitorPairIterator = new CompetitorPairIterator<>(Arrays.asList(competitors));
    }

    public CompetitorPairIterator getCompetitorPairIterator() {
        return competitorPairIterator;
    }

    public void setNewGameListener(NewGameListener newGameListener) {
        this.newGameListener = newGameListener;
    }

    public Game createNewGame(CompetitorPair pair) {
        game = gameFactory.apply(pair.competitor1, pair.competitor2);
        newGameListener.newGameCreated(game);
        return game;
    }

    /**
     *
     * iterates through all competitors in the game and adds their score to the score map.
     * if competitors wasn't previously in the score map then add them and their score
     * @param game: game containging competitors from which to take score from. OBS game.isGameOver() must return true
     */
    public void storeRating(Game<T, M> game) {
        if ( ! game.isGameOver()){
            throw new IllegalStateException("Game isn't over");
        }
        for (Map.Entry<Competitor<T, M>, Double> competitorScoreEntry : game.getResults().entrySet()) {
            Double addedRating = competitorScoreEntry.getValue();

            if (competitorScoreMap.containsKey(competitorScoreEntry.getKey())) {
                Double newRating = competitorScoreMap.get(competitorScoreEntry.getKey()) + addedRating;

                competitorScoreMap.replace(
                        competitorScoreEntry.getKey(),
                        newRating
                );

            } else {
                competitorScoreMap.put(competitorScoreEntry.getKey(), competitorScoreEntry.getValue());

            }
        }
    }

    public static class CompetitorPair<T,M> {
        public final Competitor<T,M> competitor1;
        public final Competitor<T,M> competitor2;

        public CompetitorPair(Competitor<T, M> competitor1, Competitor<T, M> competitor2) {
            this.competitor1 = Objects.requireNonNull(competitor1);
            this.competitor2 = Objects.requireNonNull(competitor2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CompetitorPair that = (CompetitorPair) o;

            if (!competitor1.equals(that.competitor1)) return false;
            if (!competitor2.equals(that.competitor2)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = competitor1.hashCode();
            result = 31 * result + competitor2.hashCode();
            return result;
        }
    }
    public static class CompetitorPairIterator<T,M> implements Iterator<CompetitorPair<T,M>> {

        private List<Competitor<T,M>> competitors = new ArrayList<>();
        private int competitor1 = 0;
        private int competitor2 = 1;

        public CompetitorPairIterator(Collection<Competitor<T, M>> competitors) {
            this.competitors.addAll(competitors);
        }

        @Override
        public boolean hasNext() {
            return competitor1 < competitors.size() - 1 ;
        }

        @Override
        public CompetitorPair next() {
            if (! hasNext()){
                throw new NoSuchElementException();
            }

            CompetitorPair round = new CompetitorPair(competitors.get(competitor1), competitors.get(competitor2));

            if (competitor2 >= competitors.size() - 1) {
                competitor1++;
                competitor2 = competitor1;
            }
            competitor2++;
            return round;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove Round from iterator");
        }
    }
}
