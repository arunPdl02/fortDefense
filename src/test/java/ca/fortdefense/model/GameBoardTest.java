package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    @Test
    void constructor_initializesAllCellsEmptyAndUnshot() {
        GameBoard board = new GameBoard();

        for (int r = 0; r < GameBoard.NUMBER_ROWS; r++) {
            for (int c = 0; c < GameBoard.NUMBER_COLS; c++) {
                Coordinate pos = new Coordinate(r, c);
                assertFalse(board.hasCellBeenShot(pos), "Expected unshot at (" + r + "," + c + ")");
                assertFalse(board.cellContainsEnemy(pos), "Expected empty at (" + r + "," + c + ")");
                assertEquals(0, board.getCellState(pos).getFortNumberAtCell(), "Expected enemy #0 at (" + r + "," + c + ")");
            }
        }
    }

    @Test
    void recordUserShot_marksCellAsShot_onlyAffectsThatCell() {
        GameBoard board = new GameBoard();
        Coordinate target = new Coordinate(2, 3);
        Coordinate other = new Coordinate(2, 4);

        assertFalse(board.hasCellBeenShot(target));
        assertFalse(board.hasCellBeenShot(other));

        board.recordUserShot(target);

        assertTrue(board.hasCellBeenShot(target));
        assertFalse(board.hasCellBeenShot(other));
    }

    @Test
    void recordUserShot_preservesEnemyNumberIfPresent() {
        GameBoard board = new GameBoard();
        Coordinate pos = new Coordinate(1, 1);

        board.recordEnemyInCell(pos, 7);
        assertTrue(board.cellContainsEnemy(pos));
        assertEquals(7, board.getCellState(pos).getFortNumberAtCell());
        assertFalse(board.hasCellBeenShot(pos));

        board.recordUserShot(pos);

        assertTrue(board.hasCellBeenShot(pos));
        assertTrue(board.cellContainsEnemy(pos));
        assertEquals(7, board.getCellState(pos).getFortNumberAtCell());
    }

    @Test
    void cellOpenForEnemy_falseWhenOutOfBounds() {
        GameBoard board = new GameBoard();

        assertFalse(board.cellOpenForEnemy(new Coordinate(-1, 0)));
        assertFalse(board.cellOpenForEnemy(new Coordinate(0, -1)));
        assertFalse(board.cellOpenForEnemy(new Coordinate(GameBoard.NUMBER_ROWS, 0)));
        assertFalse(board.cellOpenForEnemy(new Coordinate(0, GameBoard.NUMBER_COLS)));
    }

    @Test
    void cellOpenForEnemy_trueWhenInBoundsAndEmpty() {
        GameBoard board = new GameBoard();
        assertTrue(board.cellOpenForEnemy(new Coordinate(0, 0)));
        assertTrue(board.cellOpenForEnemy(new Coordinate(9, 9)));
    }

    @Test
    void cellOpenForEnemy_falseWhenCellContainsEnemy() {
        GameBoard board = new GameBoard();
        Coordinate pos = new Coordinate(4, 4);

        assertTrue(board.cellOpenForEnemy(pos));

        board.recordEnemyInCell(pos, 2);

        assertFalse(board.cellOpenForEnemy(pos));
        assertTrue(board.cellContainsEnemy(pos));
    }

    @Test
    void recordEnemyInCell_setsEnemyNumber_preservesShotState() {
        GameBoard board = new GameBoard();
        Coordinate pos = new Coordinate(6, 6);

        // Make it shot first
        board.recordUserShot(pos);
        assertTrue(board.hasCellBeenShot(pos));
        assertFalse(board.cellContainsEnemy(pos));

        // Place enemy number
        board.recordEnemyInCell(pos, 9);

        assertTrue(board.cellContainsEnemy(pos));
        assertEquals(9, board.getCellState(pos).getFortNumberAtCell());
        assertTrue(board.hasCellBeenShot(pos), "Shot state should be preserved when adding enemy");
    }
}
