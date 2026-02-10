package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnemyTest {

    private static final int[] DAMAGE = {0, 1, 2, 5, 20, 20};

    @Test
    void constructor_placesEnemyByRecordingEnemyNumberInEachOccupiedCell() throws Exception {
        GameBoard board = mock(GameBoard.class);

        when(board.cellOpenForEnemy(any(Coordinate.class))).thenReturn(true);

        int enemyNumber = 3;
        Enemy enemy = new Enemy(board, enemyNumber);

        int shapeCellCount = getShapeCellCount(enemy);

        verify(board, times(shapeCellCount)).recordEnemyInCell(any(Coordinate.class), eq(enemyNumber));
        assertTrue(enemy.getUndamagedCellCount() >= 0);
    }

    @Test
    void getUndamagedCellCount_countsUnshotCellsAmongFortCells() throws Exception {
        GameBoard board = mock(GameBoard.class);
        when(board.cellOpenForEnemy(any(Coordinate.class))).thenReturn(true);

        Enemy enemy = new Enemy(board, 1);

        List<Coordinate> fortCells = getAbsoluteFortCellsAsList(enemy);

        // Mark exactly one fort cell as shot; others unshot.
        Set<String> shotKeys = new HashSet<>();
        shotKeys.add(keyOf(fortCells.get(0)));

        when(board.hasCellBeenShot(any(Coordinate.class))).thenAnswer(invocation -> {
            Coordinate c = invocation.getArgument(0);
            return shotKeys.contains(keyOf(c));
        });

        int expectedUndamaged = fortCells.size() - 1;
        assertEquals(expectedUndamaged, enemy.getUndamagedCellCount());
    }


    @Test
    void isFortDestroyed_trueWhenAllFortCellsShot_falseOtherwise() throws Exception {
        GameBoard board = mock(GameBoard.class);
        when(board.cellOpenForEnemy(any(Coordinate.class))).thenReturn(true);

        Enemy enemy = new Enemy(board, 1);

        when(board.hasCellBeenShot(any(Coordinate.class))).thenReturn(false);
        assertFalse(enemy.isFortDestroyed());

        when(board.hasCellBeenShot(any(Coordinate.class))).thenReturn(true);
        assertTrue(enemy.isFortDestroyed());
        assertEquals(0, enemy.getUndamagedCellCount());
    }

    @Test
    void getShotDamage_matchesDamageTableForCountsWithinSupportedRange() throws Exception {
        GameBoard board = mock(GameBoard.class);
        when(board.cellOpenForEnemy(any(Coordinate.class))).thenReturn(true);

        Enemy enemy = new Enemy(board, 1);
        List<Coordinate> fortCells = getAbsoluteFortCellsAsList(enemy);
        int n = fortCells.size();

        int maxIndex = DAMAGE.length - 1;
        int maxTestable = Math.min(n, maxIndex);

        for (int undamaged = 0; undamaged <= maxTestable; undamaged++) {
            int toShot = n - undamaged;

            Set<String> shotKeys = new HashSet<>();
            for (int i = 0; i < toShot && i < fortCells.size(); i++) {
                shotKeys.add(keyOf(fortCells.get(i)));
            }

            when(board.hasCellBeenShot(any(Coordinate.class))).thenAnswer(invocation -> {
                Coordinate c = invocation.getArgument(0);
                return shotKeys.contains(keyOf(c));
            });

            assertEquals(undamaged, enemy.getUndamagedCellCount(), "Unexpected undamaged count setup");
            assertEquals(DAMAGE[undamaged], enemy.getShotDamage(), "Damage mismatch for undamaged=" + undamaged);
        }
    }

    // ----------------- Reflection helpers -----------------

    private static int getShapeCellCount(Enemy enemy) throws Exception {
        Polyomino shape = getEnemyShape(enemy);
        return shape.getCellLocations().size();
    }

    private static List<Coordinate> getAbsoluteFortCellsAsList(Enemy enemy) throws Exception {
        Polyomino shape = getEnemyShape(enemy);
        Coordinate start = getEnemyStartCell(enemy);

        // Convert shape cells (Collection) into a List so tests can index deterministically.
        List<Coordinate> relCells = new ArrayList<>(shape.getCellLocations());
        return relCells.stream()
                .map(start::add)
                .toList();
    }

    private static Polyomino getEnemyShape(Enemy enemy) throws Exception {
        Field f = Enemy.class.getDeclaredField("shape");
        f.setAccessible(true);
        return (Polyomino) f.get(enemy);
    }

    private static Coordinate getEnemyStartCell(Enemy enemy) throws Exception {
        Field f = Enemy.class.getDeclaredField("startCell");
        f.setAccessible(true);
        return (Coordinate) f.get(enemy);
    }

    private static String keyOf(Coordinate c) {
        return c.getRowIndex() + "," + c.getColIndex();
    }
}
