package ca.fortdefense.model;

import java.security.InvalidParameterException;

/**
 * Represents a location on the game board using zero-based row and column indices.
 *
 * <p>This class supports two construction modes:</p>
 * <ul>
 *   <li>{@link #Coordinate(int, int)}: directly sets zero-based row/column indices.</li>
 *   <li>{@link #Coordinate(String)}: parses a human-readable coordinate such as {@code "A1"} or {@code "B10"}.</li>
 * </ul>
 *
 * <p>String format rules:</p>
 * <ul>
 *   <li>The first character is a letter representing the row (A = 0, B = 1, ...).</li>
 *   <li>The remaining characters are an integer representing the column using one-based indexing (1 maps to 0).</li>
 * </ul>
 *
 * <p>Row/column values are validated against {@link GameBoard#NUMBER_ROWS} and {@link GameBoard#NUMBER_COLS}.</p>
 */
public class Coordinate {
    private static final int MIN_TEXT_LENGTH = 2;
    private static final int TO_ZERO_OFFSET = 1;
    private static final int COL_INDEX_IN_STRING = 1;

    private int rowIndex = 0;
    private int colIndex = 0;

    /**
     * Creates a coordinate with explicit zero-based indices.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     */
    public Coordinate(int row, int col) {
        rowIndex = row;
        colIndex = col;
    }

    /**
     * Creates a coordinate by parsing a string such as {@code "A1"} or {@code "B10"}.
     *
     * <p>The first character defines the row (A = 0, B = 1, ...). The remaining substring is parsed
     * as an integer column value (1-based) and converted to zero-based indexing.</p>
     *
     * @param input coordinate string
     * @throws InvalidParameterException if the input is too short, not parseable, or out of bounds
     */
    public Coordinate(String input) {
        if (sourceStringTooShort(input)) {
            throw new InvalidParameterException("Not enough text.");
        }

        // Extract the row (letter)
        String firstLetter = input.substring(0, COL_INDEX_IN_STRING);
        int row = charToIndex(firstLetter);

        // Extract the column (int)
        String laterCharacters = input.substring(COL_INDEX_IN_STRING);
        try {
            int col = Integer.parseInt(laterCharacters) - TO_ZERO_OFFSET;
            setRowAndCol(row, col);
        } catch (NumberFormatException exception) {
            throw new InvalidParameterException("Invalid input format.");
        }
    }

    /**
     * Checks whether the input string is shorter than the minimum supported format length.
     *
     * @param input the source string
     * @return true if the string length is less than {@value #MIN_TEXT_LENGTH}
     */
    private boolean sourceStringTooShort(String input) {
        return input.length() < MIN_TEXT_LENGTH;
    }

    /**
     * Converts a single-letter row designator into a zero-based row index.
     *
     * @param firstLetter a string containing the row letter
     * @return zero-based row index (A = 0, B = 1, ...)
     */
    private int charToIndex(String firstLetter) {
        return firstLetter.toUpperCase().charAt(0) - 'A';
    }

    /**
     * Validates and sets the row and column indices.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @throws InvalidParameterException if row/col are out of bounds
     */
    private void setRowAndCol(int row, int col) {
        rowAndColAreValid(row, col);

        rowIndex = row;
        colIndex = col;
    }

    /**
     * Validates that the provided row and column indices are within board bounds.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @throws InvalidParameterException if row/col are out of bounds
     */
    public void rowAndColAreValid(int row, int col) {
        if (row < 0 || row >= GameBoard.NUMBER_ROWS) {
            throw new InvalidParameterException("Invalid row.");
        } else if (col < 0 || col >= GameBoard.NUMBER_COLS) {
            throw new InvalidParameterException("Invalid column number.");
        }
    }

    /**
     * Returns the zero-based row index.
     *
     * @return zero-based row index
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the zero-based column index.
     *
     * @return zero-based column index
     */
    public int getColIndex() {
        return colIndex;
    }

    /**
     * Returns a human-readable representation of this coordinate.
     *
     * @return string in the form {@code "Row X  Col Y"}
     */
    public String toString() {
        return "Row " + rowIndex + "  Col " + colIndex;
    }

    /**
     * Returns a new coordinate that is the component-wise sum of this coordinate and the other.
     *
     * @param other another coordinate
     * @return new coordinate with added row and column indices
     */
    public Coordinate add(Coordinate other) {
        return new Coordinate(
                this.rowIndex + other.rowIndex,
                this.colIndex + other.colIndex
        );
    }

    /**
     * Compares coordinates by row and column indices.
     *
     * @param otherObject object to compare against
     * @return true if the other object is a Coordinate with the same row and column indices
     */
    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == this) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (!(otherObject instanceof Coordinate)) {
            return false;
        }

        Coordinate other = (Coordinate) otherObject;
        boolean sameRow = (this.rowIndex == other.rowIndex);
        boolean sameCol = (this.colIndex == other.colIndex);
        return sameRow && sameCol;
    }
}
