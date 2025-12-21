package ca.fortdefense.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represent an enemy and their fort, including its location and shape.
 * Able to describe how many points their shot earns by using
 * the GameBoard to find out where the user has shot of this enemy's fort.
 */
public class Enemy {
    private final GameBoard board;
    private Coordinate startCell;
    private final Polyomino shape = new Polyomino();
    private final int enemyNumber;


    // Exception to return when placing enemy on board fails
    public static class UnableToCreateEnemyException extends RuntimeException {
    }

    // Game designed to have damage fall off very quickly.
    private final static int[] DAMAGE_DONE_PER_UNDAMAGED_CELLS = {0, 1, 2, 5, 20, 20};

    public Enemy(GameBoard board, int enemyNumber) {
        this.board = board;
        this.enemyNumber = enemyNumber;
        placeOnBoard();
    }

    private void placeOnBoard() {
        List<Coordinate> positions = getAllPossibleLocations();

        Coordinate posFit = positions.stream()
                .filter(this::fitsOnBoardAtPosition)
                .findFirst()
                .orElseThrow();
        placeOnBoardAtPosition(posFit);
        // Same code as
//        for (Coordinate position : positions) {
//            if (fitsOnBoardAtPosition(position)) {
//                placeOnBoardAtPosition(position);
//                return;
//            }
//        }
//        throw new UnableToCreateEnemyException();
    }

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

    private List<Coordinate> getCellLocationsRelativeToBoardPosition( Coordinate position) {
        return shape.getCellLocations().stream()
                .map(position::add)
                .toList();
    }

    private boolean fitsOnBoardAtPosition(Coordinate position) {
        // TODO: Which is clearer?
        return getCellLocationsRelativeToBoardPosition(position).stream()
                .allMatch(board::cellOpenForEnemy);

//        for (Coordinate shapeCell : shape.getCellLocations()) {
//            Coordinate realCell = position.add(shapeCell);
//
//            if (!board.cellOpenForEnemy(realCell)) {
//                return false;
//            }
//        }
//        return true;
    }

    private void placeOnBoardAtPosition(Coordinate position) {
        // TODO: Which is clearer?
        // OPTION 1:
        startCell = position;
        getCellLocationsRelativeToBoardPosition(position)
                .forEach(cell -> board.recordEnemyInCell(cell, enemyNumber));

        // OPTION 2:
//        startCell = position;
//        for (Coordinate shapeCell : shape.getCellLocations()) {
//            Coordinate realCell = position.add(shapeCell);
//            board.recordEnemyInCell(realCell, enemyNumber);
//        }
    }

    public int getUndamagedCellCount() {

        // TODO: Which is clearer?
        // Option 1:
        return (int) getCellLocationsRelativeToBoardPosition(startCell).stream()
                .filter(cell -> !board.hasCellBeenShot(cell))
                .count();

        // Option 2:
//        int count = 0;
//        for (Coordinate shapeCell : shape.getCellLocations()) {
//            Coordinate realCell = startCell.add(shapeCell);
//            if (!board.hasCellBeenShot(realCell)) {
//                count++;
//            }
//        }
//        return count;
    }

    public int getShotDamage() {
        return DAMAGE_DONE_PER_UNDAMAGED_CELLS[getUndamagedCellCount()];
    }

    public boolean isFortDestroyed() {
        return getUndamagedCellCount() == 0;
    }
}
