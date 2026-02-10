package ca.fortdefense.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an enemy and its fort, including placement and damage calculation.
 *
 * <p>An {@code Enemy} places a fort onto the provided {@link GameBoard} during construction.
 * The fort's shape is represented by a {@link Polyomino}. Placement selects a random board
 * position that fits (all cells are open for the enemy) and records the enemy number into
 * each occupied cell on the board.</p>
 *
 * <p>Damage calculation is based on how many fort cells remain unshot (undamaged). As the fort
 * takes hits, damage generally decreases quickly.</p>
 */
public class Enemy {
    private final GameBoard board;
    private Coordinate startCell;
    private final Polyomino shape = new Polyomino();
    private final int enemyNumber;

    /**
     * Exception type intended for signaling that enemy placement failed.
     *
     * <p>Note: current implementation uses {@code orElseThrow()} in placement, which will
     * throw a {@link java.util.NoSuchElementException} if no position fits.</p>
     */
    public static class UnableToCreateEnemyException extends RuntimeException {
    }

    /**
     * Damage lookup table indexed by the number of undamaged cells in the fort.
     * Game is designed to have damage fall off very quickly.
     */
    private final static int[] DAMAGE_DONE_PER_UNDAMAGED_CELLS = {0, 1, 2, 5, 20, 20};

    /**
     * Creates an enemy and immediately places its fort on the provided board.
     *
     * @param board the game board used for placement and damage queries
     * @param enemyNumber the identifier recorded in occupied cells
     */
    public Enemy(GameBoard board, int enemyNumber) {
        this.board = board;
        this.enemyNumber = enemyNumber;
        placeOnBoard();
    }

    /**
     * Finds a random position on the board where the fort shape fits, then records
     * the enemy in each occupied cell.
     *
     * <p>Implementation: generates all coordinates, shuffles them, then finds the first
     * position that fits.</p>
     */
    private void placeOnBoard() {
        List<Coordinate> positions = getAllPossibleLocations();

        Coordinate posFit = positions.stream()
                .filter(this::fitsOnBoardAtPosition)
                .findFirst()
                .orElseThrow();
        placeOnBoardAtPosition(posFit);
    }

    /**
     * Generates a list of all board coordinates and shuffles them to randomize placement.
     *
     * @return shuffled list of all possible board positions
     */
    private List<Coordinate> getAllPossibleLocations() {
        List<Coordinate> list = new ArrayList<>();
        for (int row = 0; row < GameBoard.NUMBER_ROWS; row++) {
            for (int col = 0; col < GameBoard.NUMBER_COLS; col++) {
                list.add(new Coordinate(row, col));
            }
        }
        Collections.shuffle(list);
        return list;
    }

    /**
     * Computes the absolute board coordinates occupied by the fort shape when placed at
     * the given board position.
     *
     * @param position starting board position
     * @return list of absolute board coordinates for the fort's cells
     */
    private List<Coordinate> getCellLocationsRelativeToBoardPosition(Coordinate position) {
        return shape.getCellLocations().stream()
                .map(position::add)
                .toList();
    }

    /**
     * Checks whether the fort shape can be placed at the given position.
     *
     * <p>A position fits if all occupied cells are considered open by the board.</p>
     *
     * @param position candidate placement position
     * @return true if all occupied cells are open, otherwise false
     */
    private boolean fitsOnBoardAtPosition(Coordinate position) {
        return getCellLocationsRelativeToBoardPosition(position).stream()
                .allMatch(board::cellOpenForEnemy);
    }

    /**
     * Records the enemy number in each board cell occupied by the fort when placed at
     * the given position, and stores the chosen start cell.
     *
     * @param position chosen placement position
     */
    private void placeOnBoardAtPosition(Coordinate position) {
        startCell = position;
        getCellLocationsRelativeToBoardPosition(position)
                .forEach(cell -> board.recordEnemyInCell(cell, enemyNumber));
    }

    /**
     * Counts how many fort cells are not yet shot.
     *
     * @return number of undamaged (unshot) cells in the fort
     */
    public int getUndamagedCellCount() {
        return (int) getCellLocationsRelativeToBoardPosition(startCell).stream()
                .filter(cell -> !board.hasCellBeenShot(cell))
                .count();
    }

    /**
     * Returns the damage this enemy would deal based on the number of undamaged fort cells.
     *
     * @return damage value from the lookup table
     */
    public int getShotDamage() {
        return DAMAGE_DONE_PER_UNDAMAGED_CELLS[getUndamagedCellCount()];
    }

    /**
     * Returns whether the fort is destroyed (all fort cells have been shot).
     *
     * @return true if undamaged cell count is zero, otherwise false
     */
    public boolean isFortDestroyed() {
        return getUndamagedCellCount() == 0;
    }
}
