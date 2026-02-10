package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @Test
    void constructorWithInts_setsIndices() {
        Coordinate c = new Coordinate(2, 3);
        assertEquals(2, c.getRowIndex());
        assertEquals(3, c.getColIndex());
    }

    @Test
    void stringConstructor_parsesLowercaseRowLetter() {
        Coordinate c = new Coordinate("b1"); // row B -> 1, col 1 -> 0
        assertEquals(1, c.getRowIndex());
        assertEquals(0, c.getColIndex());
    }

    @Test
    void stringConstructor_parsesUppercaseRowLetter() {
        Coordinate c = new Coordinate("A1");
        assertEquals(0, c.getRowIndex());
        assertEquals(0, c.getColIndex());
    }

    @Test
    void stringConstructor_parsesMultiDigitColumn() {
        Coordinate c = new Coordinate("A10"); // col 10 -> 9
        assertEquals(0, c.getRowIndex());
        assertEquals(9, c.getColIndex());
    }

    @Test
    void stringConstructor_throwsWhenTooShort() {
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> new Coordinate("A"));
        assertEquals("Not enough text.", ex.getMessage());
    }

    @Test
    void stringConstructor_throwsWhenColumnNotANumber() {
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> new Coordinate("Axx"));
        assertEquals("Invalid input format.", ex.getMessage());
    }

    @Test
    void stringConstructor_throwsWhenColumnIsZero_oneBasedInput() {
        // "A0" becomes -1 after TO_ZERO_OFFSET, which should be invalid col
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> new Coordinate("A0"));
        assertEquals("Invalid column number.", ex.getMessage());
    }

    @Test
    void rowAndColAreValid_throwsForNegativeRow() {
        Coordinate c = new Coordinate(0, 0);
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> c.rowAndColAreValid(-1, 0));
        assertEquals("Invalid row.", ex.getMessage());
    }

    @Test
    void rowAndColAreValid_throwsForNegativeCol() {
        Coordinate c = new Coordinate(0, 0);
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> c.rowAndColAreValid(0, -1));
        assertEquals("Invalid column number.", ex.getMessage());
    }

    @Test
    void rowAndColAreValid_throwsForRowOutOfBounds() {
        Coordinate c = new Coordinate(0, 0);
        int outOfBoundsRow = GameBoard.NUMBER_ROWS; // first invalid row index
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> c.rowAndColAreValid(outOfBoundsRow, 0));
        assertEquals("Invalid row.", ex.getMessage());
    }

    @Test
    void rowAndColAreValid_throwsForColOutOfBounds() {
        Coordinate c = new Coordinate(0, 0);
        int outOfBoundsCol = GameBoard.NUMBER_COLS; // first invalid col index
        InvalidParameterException ex =
                assertThrows(InvalidParameterException.class, () -> c.rowAndColAreValid(0, outOfBoundsCol));
        assertEquals("Invalid column number.", ex.getMessage());
    }

    @Test
    void toString_matchesExpectedFormat() {
        Coordinate c = new Coordinate(4, 1);
        assertEquals("Row 4  Col 1", c.toString());
    }

    @Test
    void add_returnsNewCoordinateWithSummedIndices() {
        Coordinate a = new Coordinate(2, 3);
        Coordinate b = new Coordinate(1, 5);

        Coordinate sum = a.add(b);

        assertEquals(3, sum.getRowIndex());
        assertEquals(8, sum.getColIndex());
        // originals unchanged
        assertEquals(2, a.getRowIndex());
        assertEquals(3, a.getColIndex());
        assertEquals(1, b.getRowIndex());
        assertEquals(5, b.getColIndex());
    }

    @Test
    void equals_trueForSameIndices() {
        Coordinate a = new Coordinate(2, 3);
        Coordinate b = new Coordinate(2, 3);
        assertEquals(a, b);
        assertTrue(a.equals(b));
    }

    @Test
    void equals_falseForDifferentIndices() {
        Coordinate a = new Coordinate(2, 3);
        Coordinate b = new Coordinate(2, 4);
        assertNotEquals(a, b);
    }

    @Test
    void equals_falseForNullAndDifferentType() {
        Coordinate a = new Coordinate(2, 3);
        assertFalse(a.equals(null));
        assertFalse(a.equals("not a coordinate"));
    }

    @Test
    void equals_trueForSameReference() {
        Coordinate a = new Coordinate(2, 3);
        assertTrue(a.equals(a));
    }
}
