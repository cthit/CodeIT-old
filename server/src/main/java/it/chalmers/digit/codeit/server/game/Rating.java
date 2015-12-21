package it.chalmers.digit.codeit.server.game;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by Simon on 2/15/2015.
 */
public class Rating {
    public static final double K = 30.0;
    public static final double DIV = 2400.0;
    private static final double DEFAULT_GAMES_PLAYED = 50;

    public static double[] ratingBetapet(double[] previousRating, double[] score) {
        if (previousRating.length != score.length) {
            throw new IllegalArgumentException("parameters must have equal length");
        }
        double sumScore = Arrays.stream(score).sum();
        double sumOldRating = Arrays.stream(previousRating).sum();

        double multiply = 0.1 + 0.2*Math.exp(-0.1 * DEFAULT_GAMES_PLAYED);

        return IntStream.range(0, previousRating.length).mapToDouble(idx ->
            (score[idx] / sumScore - previousRating[idx] / sumOldRating) * previousRating[idx] * multiply
        ).toArray();
    }

    public static void main(String[] args) {
        // Testing some example values
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        while (true) {
            for (int i = 0; i < 20; i++) {
                double[] previous = new double[]{ 1400 + random.nextDouble() * 200, 1400 + random.nextDouble() * 200, 1400 + random.nextDouble() * 200 };
                double a = i <= 10 ? 10 : (20 - i);
                double b = i >  10 ? 10 : i;
                double[] score = new double[]{ a, b, random.nextInt(10) };
                System.out.println("Previous " + Arrays.toString(previous));
                System.out.println("Score    " + Arrays.toString(score));
                System.out.println("Result   " + Arrays.toString(ratingBetapet(previous, score)));
                System.out.println();
            }

            if (scanner.nextLine().equals("q")) {
                break;
            }
        }
    }

    public enum WinResult {
        WIN(1), LOSS(-1), DRAW(0);

        private final double winValue;

        private WinResult(double winValue) {
            this.winValue = winValue;
        }

        public double getWinValue() {
            return winValue;
        }

        public static WinResult forResult(double myScore, double opponentScore) {
            return myScore == opponentScore ? WinResult.DRAW :
                (myScore > opponentScore ? WinResult.WIN : WinResult.LOSS);
        }
    }

    public static double ratingELO(double user, double opponent, WinResult winValue) {
        double ratingDiff = user - opponent;
        double winExpected = 1 / (Math.pow(10, -ratingDiff / DIV) + 1);
        double myK = K;

        if (winValue == null) {
            return winExpected; // Calculate win expected only
        }

        double winCalc = winValue.getWinValue() / 2.0 + 0.5; // WIN = 1.0, LOSS = 0.0, DRAW = 0.5
        return 1.0 * myK * (winCalc - winExpected);
    }

    public static double[] ratingELO(double[] previousRating, double[] scores) {
        if (previousRating.length != scores.length) {
            throw new IllegalArgumentException("parameters must have equal length");
        }

        return IntStream.range(0, previousRating.length).mapToDouble(idx ->
                        IntStream.range(0, previousRating.length).filter(i -> i != idx).mapToDouble(i ->
                                        ratingELO(previousRating[idx], previousRating[i], WinResult.forResult(scores[idx], scores[i]))
                        ).sum()
        ).toArray();
    }

}
