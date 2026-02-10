package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTrackerTest {

    @Test
    void initialScore_isZero() {
        ScoreTracker tracker = new ScoreTracker();
        assertEquals(0, tracker.getScore());
        assertFalse(tracker.hasWon());
    }

    @Test
    void addScore_increasesScoreByPoints() {
        ScoreTracker tracker = new ScoreTracker();

        tracker.addScore(10);
        assertEquals(10, tracker.getScore());
        assertFalse(tracker.hasWon());

        tracker.addScore(15);
        assertEquals(25, tracker.getScore());
        assertFalse(tracker.hasWon());
    }

    @Test
    void hasWon_trueWhenScoreEqualsMaxScore() {
        ScoreTracker tracker = new ScoreTracker();

        tracker.addScore(ScoreTracker.MAX_SCORE);
        assertEquals(ScoreTracker.MAX_SCORE, tracker.getScore());
        assertTrue(tracker.hasWon());
    }

    @Test
    void hasWon_trueWhenScoreExceedsMaxScore() {
        ScoreTracker tracker = new ScoreTracker();

        tracker.addScore(ScoreTracker.MAX_SCORE + 1);
        assertTrue(tracker.hasWon());
        assertTrue(tracker.getScore() > ScoreTracker.MAX_SCORE);
    }

    @Test
    void hasWon_falseWhenScoreJustBelowMaxScore() {
        ScoreTracker tracker = new ScoreTracker();

        tracker.addScore(ScoreTracker.MAX_SCORE - 1);
        assertEquals(ScoreTracker.MAX_SCORE - 1, tracker.getScore());
        assertFalse(tracker.hasWon());
    }

    @Test
    void addScore_allowsMultipleAddsToReachMaxScore() {
        ScoreTracker tracker = new ScoreTracker();

        tracker.addScore(1000);
        tracker.addScore(1000);
        assertFalse(tracker.hasWon());

        tracker.addScore(500);
        assertTrue(tracker.hasWon());
        assertEquals(2500, tracker.getScore());
    }
}
