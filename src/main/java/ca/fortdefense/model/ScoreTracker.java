package ca.fortdefense.model;

/**
 * Tracks the opponent's cumulative score.
 *
 * <p>The score increases over time as enemies deal damage. The opponent is considered to have won
 * once the score reaches or exceeds {@link #MAX_SCORE}.</p>
 */
public class ScoreTracker {
    /** Score threshold at which the opponent wins. */
    public static final int MAX_SCORE = 2500;

    private int score = 0;

    /**
     * Returns the current score.
     *
     * @return current cumulative score
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds points to the current score.
     *
     * @param points number of points to add
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Returns whether the opponent has won based on the current score.
     *
     * @return true if {@code score >= MAX_SCORE}, otherwise false
     */
    public boolean hasWon() {
        return score >= MAX_SCORE;
    }
}
