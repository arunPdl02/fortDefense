package ca.fortdefense.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void hasFort_falseWhenEnemyNumberIsZero() {
        Cell cell = new Cell(false, 0);
        assertFalse(cell.hasFort());
    }

    @Test
    void hasFort_trueWhenEnemyNumberIsNonZero() {
        Cell cell = new Cell(false, 2);
        assertTrue(cell.hasFort());
    }

    @Test
    void hasBeenShot_reflectsConstructorValue() {
        assertTrue(new Cell(true, 0).hasBeenShot());
        assertFalse(new Cell(false, 0).hasBeenShot());
    }

    @Test
    void isHidden_isInverseOfHasBeenShot() {
        Cell shot = new Cell(true, 0);
        Cell notShot = new Cell(false, 0);

        assertFalse(shot.isHidden());
        assertTrue(notShot.isHidden());
    }

    @Test
    void makeHasBeenShot_returnsNewCellMarkedShot_preservesEnemyNumber() {
        Cell original = new Cell(false, 3);
        Cell updated = original.makeHasBeenShot();

        // new state
        assertTrue(updated.hasBeenShot());
        assertEquals(3, updated.getFortNumberAtCell());
        assertTrue(updated.hasFort());

        // original unchanged (immutability check via observable behavior)
        assertFalse(original.hasBeenShot());
        assertEquals(3, original.getFortNumberAtCell());
    }

    @Test
    void makeHasBeenShot_idempotentWhenAlreadyShot() {
        Cell original = new Cell(true, 4);
        Cell updated = original.makeHasBeenShot();

        assertTrue(updated.hasBeenShot());
        assertEquals(4, updated.getFortNumberAtCell());
    }

    @Test
    void makeContainEnemy_returnsNewCell_preservesShotState_updatesEnemyNumber() {
        Cell original = new Cell(false, 0);
        Cell updated = original.makeContainEnemy(9);

        assertFalse(updated.hasBeenShot());
        assertEquals(9, updated.getFortNumberAtCell());
        assertTrue(updated.hasFort());

        // original unchanged
        assertFalse(original.hasBeenShot());
        assertEquals(0, original.getFortNumberAtCell());
        assertFalse(original.hasFort());
    }

    @Test
    void makeContainEnemy_canClearEnemyBySettingZero() {
        Cell original = new Cell(true, 7);
        Cell cleared = original.makeContainEnemy(0);

        assertTrue(cleared.hasBeenShot());
        assertEquals(0, cleared.getFortNumberAtCell());
        assertFalse(cleared.hasFort());
    }
}
