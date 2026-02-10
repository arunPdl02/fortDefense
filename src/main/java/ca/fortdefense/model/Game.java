package ca.fortdefense.model;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Manages the Fort Defense game state.
 *
 * <p>This class owns the core game state, including:</p>
 * <ul>
 *   <li>A {@link GameBoard} tracking shots and enemy placement</li>
 *   <li>A list of {@link Enemy} instances representing enemy forts</li>
 *   <li>A score tracker for enemy damage dealt over time</li>
 * </ul>
 *
 * <p>Game flow:</p>
 * <ol>
 *   <li>Construct a game with {@code numEnemies}, which places enemies on the board.</li>
 *   <li>Player records shots via {@link #recordPlayerShot(Coordinate)}.</li>
 *   <li>Enemies fire via {@link #fireEnemyShots()}, which adds damage to the enemy score.</li>
 * </ol>
 */
public class Game {

    private final ScoreTracker enemyScoreTracker = new ScoreTracker();
    private final GameBoard board = new GameBoard();
    private final List<Enemy> enemies = new ArrayList<>();

    private List<Integer> latestEnemyDamages;
    private boolean lastPlayerShotHit;
    private final int NUMBER_OF_ENEMIES;

    /**
     * Creates a new game and places the requested number of enemies on the board.
     *
     * @param numEnemies number of enemies (forts) to create and place
     */
    public Game(int numEnemies) {
        NUMBER_OF_ENEMIES = numEnemies;
        IntStream.range(1, NUMBER_OF_ENEMIES + 1)
                .forEach(enemyNum -> enemies.add(new Enemy(board, enemyNum)));
    }

    /**
     * Returns true if the user has won (all enemy forts destroyed).
     *
     * @return true if all {@link Enemy} forts are destroyed
     */
    public boolean hasUserWon() {
        return enemies.stream().allMatch(Enemy::isFortDestroyed);
    }

    /**
     * Returns true if the user has lost (enemy score tracker indicates a win condition).
     *
     * @return true if the enemy score tracker has reached its win condition
     */
    public boolean hasUserLost() {
        return enemyScoreTracker.hasWon();
    }

    /**
     * Returns the current total enemy score (damage dealt to the player).
     *
     * @return total enemy points
     */
    public int getEnemyPoints() {
        return enemyScoreTracker.getScore();
    }

    /**
     * Records a player shot at the given cell and updates whether that shot hit an enemy.
     *
     * @param cell target coordinate
     */
    public void recordPlayerShot(Coordinate cell) {
        board.recordUserShot(cell);
        lastPlayerShotHit = board.cellContainsEnemy(cell);
    }

    /**
     * Returns whether the most recent player shot hit an enemy fort.
     *
     * @return true if the last recorded shot hit a fort cell
     */
    public boolean didLastPlayerShotHit() {
        return lastPlayerShotHit;
    }

    /**
     * Returns the current state of a cell on the game board.
     *
     * @param cell coordinate to query
     * @return {@link Cell} state at that coordinate
     */
    public Cell getCellState(Coordinate cell) {
        return board.getCellState(cell);
    }

    /**
     * Triggers all enemies to "fire".
     *
     * <p>Each enemy contributes damage based on its remaining undamaged fort cells.
     * Damages of 0 are ignored. Non-zero damages are added to the score tracker and
     * stored as the latest enemy damages.</p>
     */
    public void fireEnemyShots() {
        latestEnemyDamages = new ArrayList<>();
        enemies.stream()
                .mapToInt(Enemy::getShotDamage)
                .filter(dmg -> dmg > 0)
                .forEach(dmg ->
                {
                    enemyScoreTracker.addScore(dmg);
                    latestEnemyDamages.add(dmg);
                });
    }

    /**
     * Returns the number of enemies whose forts are still active (not destroyed).
     *
     * @return count of active enemies
     */
    public long getNumberOfActiveEnemies(){
        long enemiesDestroyed = enemies.stream().filter(Enemy::isFortDestroyed).count();
        return NUMBER_OF_ENEMIES - enemiesDestroyed;
    }

    /**
     * Returns the damages dealt by enemies on their most recent firing round.
     *
     * <p>If enemies have not fired yet, returns an empty array.</p>
     *
     * @return array of latest enemy damages (possibly empty)
     */
    public int[] getLatestEnemyDamages() {
        if (latestEnemyDamages == null) {
            return new int[0];
        }

        return latestEnemyDamages.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}
