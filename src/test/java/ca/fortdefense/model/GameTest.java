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

        // Before shot: should be unshot
        assertFalse(game.getCellState(cell).hasBeenShot());

        game.recordPlayerShot(cell);

        // After shot: cell must be shot
        assertTrue(game.getCellState(cell).hasBeenShot());

        // Deterministic assertion: lastShotHit should equal whether that cell has a fort
        boolean cellHasFort = game.getCellState(cell).hasFort();
        assertEquals(cellHasFort, game.didLastPlayerShotHit());
    }

    @Test
    void fireEnemyShots_populatesLatestDamages_andAccumulatesEnemyPoints() {
        int numEnemies = 5;
        Game game = new Game(numEnemies);

        // Before firing: score should be 0 and latest damages empty
        assertEquals(0, game.getEnemyPoints());
        assertArrayEquals(new int[0], game.getLatestEnemyDamages());

        game.fireEnemyShots();

        int[] damages = game.getLatestEnemyDamages();

        // With your Enemy + Polyomino settings:
        // - Each enemy starts with 5 undamaged cells -> getShotDamage() should be 20
        // - damage 20 is > 0 so it's included
        assertEquals(numEnemies, damages.length, "Expected one damage entry per enemy on first fire");

        for (int dmg : damages) {
            assertEquals(20, dmg, "Expected initial enemy damage to be 20 when fort is undamaged");
        }

        assertEquals(20 * numEnemies, game.getEnemyPoints(), "Enemy points should equal sum of latest damages");
    }

    @Test
    void getNumberOfActiveEnemies_equalsInitialEnemyCountAtStart() {
        int numEnemies = 4;
        Game game = new Game(numEnemies);

        assertEquals(numEnemies, game.getNumberOfActiveEnemies(),
                "No forts should be destroyed at game start");
        assertFalse(game.hasUserWon(), "User should not have won at start");
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
        assertEquals(2 * 20 * numEnemies, afterSecond, "Expected score to accumulate across rounds");
    }
}
