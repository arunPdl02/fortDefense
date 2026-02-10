package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PolyominoTest {

    @Test
    void constructor_createsExactlyNumCells() {
        Polyomino p = new Polyomino();
        assertEquals(Polyomino.NUM_CELLS, p.getCellLocations().size());
    }

    @Test
    void containsOriginCell() {
        Polyomino p = new Polyomino();
        assertTrue(p.getCellLocations().contains(new Coordinate(0, 0)));
    }

    @Test
    void allCellsAreUnique() {
        Polyomino p = new Polyomino();
        Collection<Coordinate> cells = p.getCellLocations();

        Set<Coordinate> unique = new HashSet<>(cells);
        assertEquals(cells.size(), unique.size(), "Expected all coordinates to be unique");
    }

    @Test
    void isConnectedViaCardinalAdjacency() {
        Polyomino p = new Polyomino();
        Set<Coordinate> cells = new HashSet<>(p.getCellLocations());

        // BFS from origin using 4-neighborhood.
        Coordinate start = new Coordinate(0, 0);
        assertTrue(cells.contains(start), "Expected origin (0,0) to exist");

        Set<Coordinate> visited = new HashSet<>();
        Deque<Coordinate> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.removeFirst();

            for (Coordinate n : neighbors4(current)) {
                if (cells.contains(n) && visited.add(n)) {
                    queue.addLast(n);
                }
            }
        }

        assertEquals(cells.size(), visited.size(), "Expected the polyomino cells to form one connected component");
    }

    @Test
    void getCellLocations_returnsUnmodifiableCollection() {
        Polyomino p = new Polyomino();
        Collection<Coordinate> view = p.getCellLocations();

        assertThrows(UnsupportedOperationException.class, () -> view.add(new Coordinate(99, 99)));
    }

    // ---------- helpers ----------

    private static List<Coordinate> neighbors4(Coordinate c) {
        int r = c.getRowIndex();
        int col = c.getColIndex();
        return List.of(
                new Coordinate(r + 1, col),
                new Coordinate(r - 1, col),
                new Coordinate(r, col + 1),
                new Coordinate(r, col - 1)
        );
    }
}
