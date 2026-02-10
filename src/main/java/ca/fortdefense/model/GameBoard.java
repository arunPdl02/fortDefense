package ca.fortdefense.model;

/**
 * Manages the game board state, tracking:
 * <ul>
 *   <li>Which cells contain enemy forts (by enemy number)</li>
 *   <li>Which cells have been shot by the user</li>
 * </ul>
 *
 * <p>The board is represented as a 2D array of immutable {@link Cell} objects. Updates are applied
 * by replacing a cell with a new {@link Cell} instance.</p>
 */
public class GameBoard {
    /** Number of rows on the board. */
    public static final int NUMBER_ROWS = 10;

    /** Number of columns on the board. */
    public static final int NUMBER_COLS = 10;

    /** Internal board storage indexed by [row][col]. */
    private final Cell[][] board = new Cell[NUMBER_ROWS][NUMBER_COLS];

    /**
     * Creates a new board initialized with empty, unshot cells.
     * Each cell starts with no fort (enemy number 0) and not shot.
     */
    public GameBoard() {
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLS; col++) {
                board[row][col] = new Cell(false, 0);
            }
        }
    }

    /**
     * Returns the {@link Cell} object representing the current state at the given coordinate.
     *
     * @param cell board coordinate (zero-based)
     * @return current {@link Cell} state at that coordinate
     */
    public Cell getCellState(Coordinate cell) {
        int row = cell.getRowIndex();
        int col = cell.getColIndex();
        return board[row][col];
    }

    /**
     * Returns whether the given cell has been shot by the user.
     *
     * @param cell board coordinate (zero-based)
     * @return true if the cell has been shot, otherwise false
     */
    public boolean hasCellBeenShot(Coordinate cell) {
        int row = cell.getRowIndex();
        int col = cell.getColIndex();
        return board[row][col].hasBeenShot();
    }

    /**
     * Returns whether the given cell currently contains an enemy fort.
     *
     * @param cell board coordinate (zero-based)
     * @return true if the cell contains a fort, otherwise false
     */
    public boolean cellContainsEnemy(Coordinate cell) {
        int row = cell.getRowIndex();
        int col = cell.getColIndex();
        return board[row][col].hasFort();
    }

    /**
     * Records a user shot at the given coordinate by marking that cell as shot.
     *
     * <p>Because {@link Cell} is immutable, this method replaces the stored cell with a new instance.</p>
     *
     * @param pos board coordinate (zero-based)
     */
    public void recordUserShot(Coordinate pos) {
        Cell current = board[pos.getRowIndex()][pos.getColIndex()];
        board[pos.getRowIndex()][pos.getColIndex()] = current.makeHasBeenShot();
    }

    /**
     * Returns whether a cell is available for enemy placement.
     *
     * <p>A cell is open if it is within bounds and does not currently contain an enemy fort.</p>
     *
     * @param cell board coordinate (may be out of bounds)
     * @return true if the coordinate is in bounds and does not contain an enemy, otherwise false
     */
    public boolean cellOpenForEnemy(Coordinate cell) {
        int row = cell.getRowIndex();
        int col = cell.getColIndex();
        // Row out of bounds?
        if (row < 0 || row >= NUMBER_ROWS) {
            return false;
        }
        // Column out of bounds?
        if (col < 0 || col >= NUMBER_COLS) {
            return false;
        }
        // Has enemy?
        return !cellContainsEnemy(cell);
    }

    /**
     * Records an enemy fort number in the given cell.
     *
     * <p>Precondition: {@link #cellOpenForEnemy(Coordinate)} must be true. This is enforced by an
     * {@code assert} statement (requires assertions to be enabled at runtime to throw).</p>
     *
     * @param cell board coordinate (zero-based, in bounds)
     * @param enemyNumberAtCell the enemy/fort number to store (non-zero means a fort is present)
     */
    public void recordEnemyInCell(Coordinate cell, int enemyNumberAtCell) {
        assert cellOpenForEnemy(cell);

        int row = cell.getRowIndex();
        int col = cell.getColIndex();
        board[row][col] = board[row][col].makeContainEnemy(enemyNumberAtCell);
    }
}
