package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void getLatestEnemyDamages_returnsEmptyArrayBeforeEnemiesFire() {
        Game game = new Game(3);
        assertArrayEquals(new int[0], game.getLatestEnemyDamages());
    }

    @Test
    void recordPlayerShot_marksCellShot_andLastShotHitMatchesCellFortPresence() {
        Game game = new Game(3);

        Coordinate cell = new Coordinate(0, 0);

        assertFalse(game.getCellState(cell).hasBeenShot());

        game.recordPlayerShot(cell);

        assertTrue(game.getCellState(cell).hasBeenShot());

        boolean cellHasFort = game.getCellState(cell).hasFort();
        assertEquals(cellHasFort, game.didLastPlayerShotHit());
    }

    @Test
    void fireEnemyShots_populatesLatestDamages_andAccumulatesEnemyPoints() {
        int numEnemies = 5;
        Game game = new Game(numEnemies);

        assertEquals(0, game.getEnemyPoints());
        assertArrayEquals(new int[0], game.getLatestEnemyDamages());

        game.fireEnemyShots();

        int[] damages = game.getLatestEnemyDamages();

        // With Polyomino.NUM_CELLS = 5, each enemy starts fully undamaged -> damage should be 20.
        assertEquals(numEnemies, damages.length, "Expected one damage entry per enemy on first fire");

        int sum = 0;
        for (int dmg : damages) {
            assertEquals(20, dmg, "Expected initial enemy damage to be 20 when fort is undamaged");
            sum += dmg;
        }

        assertEquals(sum, game.getEnemyPoints(), "Enemy points should equal sum of latest damages");
    }

    @Test
    void getNumberOfActiveEnemies_equalsInitialEnemyCountAtStart() {
        int numEnemies = 4;
        Game game = new Game(numEnemies);

        assertEquals(numEnemies, game.getNumberOfActiveEnemies());
        assertFalse(game.hasUserWon());
    }

    @Test
    void fireEnemyShots_canBeCalledMultipleTimes_andScoreAccumulates() {
        int numEnemies = 3;
        Game game = new Game(numEnemies);

        game.fireEnemyShots();
        int afterFirst = game.getEnemyPoints();
        assertEquals(20 * numEnemies, afterFirst);

        game.fireEnemyShots();
        int afterSecond = game.getEnemyPoints();
        assertEquals(2 * 20 * numEnemies, afterSecond);
    }

    @Test
    void hasUserLost_falseInitially_andTrueOnceScoreReachesMax() {
        // Use enough enemies so we can reach MAX_SCORE quickly with repeated firing.
        // Each firing adds 20 per enemy (initially), so 20 * 5 = 100 points per round.
        int numEnemies = 5;
        Game game = new Game(numEnemies);

        assertFalse(game.hasUserLost(), "Should not be lost at start");

        // Fire until we reach or exceed MAX_SCORE.
        while (game.getEnemyPoints() < ScoreTracker.MAX_SCORE) {
            game.fireEnemyShots();
        }

        assertTrue(game.hasUserLost(), "Should be lost once enemy points reach MAX_SCORE");
        assertTrue(game.getEnemyPoints() >= ScoreTracker.MAX_SCORE);
    }
}
