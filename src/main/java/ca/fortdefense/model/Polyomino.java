package ca.fortdefense.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents an N-cell connected shape (a polyomino) built by randomized growth.
 *
 * <p>The shape starts with a single cell at relative coordinate {@code (0,0)} and grows
 * by adding one new orthogonally-adjacent cell at a time until {@link #NUM_CELLS} cells
 * are reached.</p>
 *
 * <p>Growth algorithm (high-level):</p>
 * <ul>
 *   <li>Randomly choose an existing cell to grow from.</li>
 *   <li>Randomly try the four cardinal directions (up/down/left/right).</li>
 *   <li>Add the first new coordinate that is not already part of the shape.</li>
 * </ul>
 *
 * <p>The coordinates returned by {@link #getCellLocations()} are relative, not tied to a board.
 * A caller can translate them to absolute positions by adding an offset coordinate.</p>
 */
public class Polyomino {
    /** Number of cells in the polyomino. */
    public static final int NUM_CELLS = 5;

    /** Number of cardinal directions considered during growth (up/down/left/right). */
    private static final int NUM_DIRECTIONS = 4;

    /** Internal mutable list of occupied relative coordinates. */
    private final List<Coordinate> cells = new ArrayList<>();

    /**
     * Creates a new polyomino by starting at {@code (0,0)} and repeatedly growing
     * until {@link #NUM_CELLS} cells exist.
     */
    public Polyomino() {
        cells.add(new Coordinate(0, 0));
        for (int i = 1; i < NUM_CELLS; i++) {
            growPolyomino();
        }
    }

    /**
     * Adds exactly one new cell to the polyomino by expanding from an existing cell.
     *
     * <p>For each candidate base cell (random order), directions are tried (random order)
     * until an unoccupied coordinate is found and added.</p>
     */
    private void growPolyomino() {
        // Pick random cell to grow from
        List<Integer> cellsToGrowFrom = generatePermutationOf0ToNMinus1(cells.size());
        for (int cellGrowFormIdx : cellsToGrowFrom) {
            int growFromRow = cells.get(cellGrowFormIdx).getRowIndex();
            int growFromCol = cells.get(cellGrowFormIdx).getColIndex();

            // Pick a random direction:
            List<Integer> directions = generatePermutationOf0ToNMinus1(NUM_DIRECTIONS);
            for (int direction : directions) {
                int newRow = growFromRow;
                int newCol = growFromCol;

                // Numbers 0-3 have no meaning, they are just random possibilities.
                switch (direction) {
                    case 0: newRow++; break;
                    case 1: newRow--; break;
                    case 2: newCol++; break;
                    case 3: newCol--; break;
                    default:
                        assert false;
                }

                Coordinate newLoc = new Coordinate(newRow, newCol);
                if (!cells.contains(newLoc)) {
                    cells.add(newLoc);
                    return;
                }
            }
        }
        assert false;
    }

    /**
     * Generates a random permutation of integers from {@code 0} (inclusive) to {@code n} (exclusive).
     *
     * @param n size of the permutation
     * @return a shuffled list containing values {@code 0..n-1}
     */
    private List<Integer> generatePermutationOf0ToNMinus1(int n) {
        List<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            permutation.add(i);
        }

        Collections.shuffle(permutation);
        return permutation;
    }

    /**
     * Returns an unmodifiable view of the polyomino's relative cell coordinates.
     *
     * @return unmodifiable collection of relative {@link Coordinate}s in the polyomino
     */
    public Collection<Coordinate> getCellLocations() {
        return Collections.unmodifiableCollection(cells);
    }
}
