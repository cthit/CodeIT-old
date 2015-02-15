package game;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import it.tejp.codeit.api.GameMechanic;

import java.util.*;
import java.util.function.BiFunction;


public class Model<T, M> {
    private final BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory;
    private final List<Competitor<T, M>> competitors = new ArrayList<>();
    private final CompetitorPairIterator<T,M> competitorPairIterator;
    private Game<T, M> game;

    private final Map<String, Double> rating = new HashMap<>();

    public Model(BiFunction<Competitor<T, M>, Competitor<T, M>, Game> gameFactory, Competitor<T,M>... competitors) {
        this.gameFactory = gameFactory;

        competitorPairIterator = new CompetitorPairIterator<>(Arrays.asList(competitors));
    }

    public Game createNewGame(CompetitorPair pair) {
        return createNewGame(pair.competitor1, pair.competitor2);
    }

    public Game<T,M> createNewGame(Competitor<T,M> competitor1, Competitor<T,M> competitor2) {
        game = gameFactory.apply(competitor1, competitor2);
        return game;
    }

    /**
     *
     * @param teamName
     * @param gameMechanic:
     *
     * add new competitor if teamName isn't already claimed.
     * if it is claimed by a previous competitor, replace that competitors gameMechanic
     *
     */
    public void handleContributionFromCompetitor(String teamName, GameMechanic<T,M> gameMechanic) {
        Competitor<T, M> competitorToEvaluate = null;
        for (Competitor<T, M> competitor : competitors) {
            if (teamName.equals(competitor.getTeamName())) {
                competitor.setGameMechanic(gameMechanic);
                competitorToEvaluate = competitor;
                break;
            }
        }
        if (competitorToEvaluate == null) {
            competitorToEvaluate = new Competitor<>(teamName, gameMechanic);
        }
        competitors.add(competitorToEvaluate);
        // rating algorithm needs to run multiple times.
        for (int i = 0; i < 10; i++)
            evaluateCompetitor(competitorToEvaluate);

        updateRating(competitors);
    }

    private synchronized void updateRating(final List<Competitor<T, M>> competitors) {
        rating.clear();
        for(Competitor<T, M> competitor : competitors) {
            rating.put(competitor.getTeamName(), competitor.getRating());
            System.out.println("UpdateScore: " + competitor.getTeamName() + " " + competitor.getRating());
        }
    }

    public synchronized Map<String, Double> getRating() {
        Map<String, Double> ret = new HashMap<>();
        Iterator it = rating.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Double> pairs = (Map.Entry<String, Double>)it.next();
            ret.put(pairs.getKey(), pairs.getValue());
        }
        return ret;
    }

    public void evaluateCompetitor(Competitor<T,M> competitor) {
        competitors.stream().filter(otherCompetitor -> !competitor.equals(otherCompetitor)).forEach(otherCompetitor -> {
            Game<T, M> game = createNewGame(competitor, otherCompetitor);
            while (!game.isGameOver()) {
                game.play();
            }

            double[] results = game.getResults();
//            final double[] calculatedRating = Rating.ratingBetapet(new double[]{competitor.getRating(), otherCompetitor.getRating()}, results);
            final double[] calculatedRating = Rating.ratingELO(new double[]{competitor.getRating(), otherCompetitor.getRating()}, results);
            competitor.addRating(calculatedRating[0]);
            otherCompetitor.addRating(calculatedRating[1]);
        });
    }

    public void evaluateCompetitors() {
        competitors.forEach(this::evaluateCompetitor);

    }

    public CompetitorPairIterator getCompetitorPairIterator() {
        return competitorPairIterator;
    }

    public List<Competitor<T,M>> getCompetitors() {
        return competitors;
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

            return competitor1.equals(that.competitor1) && competitor2.equals(that.competitor2);

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
