package ca.fortdefense.model;

/**
 * Manage the points of the other team
 */
public class ScoreTracker {
    public static final int MAX_SCORE = 2500;

    private int score = 0;

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }

    public boolean hasWon() {
        return score >= MAX_SCORE;
    }
}
