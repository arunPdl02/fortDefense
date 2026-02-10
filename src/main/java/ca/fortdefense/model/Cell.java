package ca.fortdefense.model;

/**
 * Represents the state of a single cell on the game board.
 *
 * <p>This is an immutable value object. Any "modification" returns a new {@code Cell}
 * instance rather than mutating the existing one.</p>
 *
 * <p>Concepts:</p>
 * <ul>
 *   <li><b>Shot state</b>: whether the player has fired at this cell.</li>
 *   <li><b>Fort state</b>: whether this cell contains an enemy fort (represented by a non-zero number).</li>
 * </ul>
 */
public class Cell {
    private final boolean hasBeenShot;
    private final int enemyNumberAtCell;

    /**
     * Creates a new cell with the provided state.
     *
     * @param isShot whether the cell has been shot
     * @param enemyNumberAtCell the enemy/fort number at this cell (0 means no fort)
     */
    public Cell(boolean isShot, int enemyNumberAtCell) {
        this.hasBeenShot = isShot;
        this.enemyNumberAtCell = enemyNumberAtCell;
    }

    /**
     * Returns true if this cell contains a fort.
     *
     * @return true if {@code enemyNumberAtCell != 0}, otherwise false
     */
    public boolean hasFort() {
        return enemyNumberAtCell != 0;
    }

    /**
     * Returns whether this cell has been shot.
     *
     * @return true if the cell has been shot, otherwise false
     */
    public boolean hasBeenShot() {
        return hasBeenShot;
    }

    /**
     * Returns whether this cell is hidden from the player.
     *
     * <p>A cell is hidden if it has not been shot yet.</p>
     *
     * @return true if the cell has not been shot, otherwise false
     */
    public boolean isHidden() {
        return !hasBeenShot;
    }

    /**
     * Returns a new {@code Cell} instance that is marked as shot,
     * preserving the current fort/enemy number.
     *
     * @return a new {@code Cell} with {@code hasBeenShot = true}
     */
    public Cell makeHasBeenShot() {
        return new Cell(true, enemyNumberAtCell);
    }

    /**
     * Returns a new {@code Cell} instance that contains the given enemy/fort number,
     * preserving the current shot state.
     *
     * @param enemyNumber the enemy/fort number to store (0 means no fort)
     * @return a new {@code Cell} with the provided enemy number
     */
    public Cell makeContainEnemy(int enemyNumber) {
        return new Cell(hasBeenShot, enemyNumber);
    }

    /**
     * Returns the enemy/fort number stored at this cell.
     *
     * @return the enemy/fort number (0 means no fort)
     */
    public int getFortNumberAtCell() {
        return enemyNumberAtCell;
    }
}
