package ca.fortdefense.mapper;

import ca.fortdefense.model.Cell;
import ca.fortdefense.model.Coordinate;
import ca.fortdefense.model.Game;
import ca.fortdefense.model.GameBoard;
import ca.fortdefense.restapi.ApiBoardDTO;
import ca.fortdefense.restapi.ApiGameDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {

    @Test
    void toApiGameDTO_mapsFieldsCorrectly() {
        Game game = mock(Game.class);

        when(game.hasUserWon()).thenReturn(true);
        when(game.hasUserLost()).thenReturn(false);
        when(game.getEnemyPoints()).thenReturn(42);
        when(game.getNumberOfActiveEnemies()).thenReturn(3L);
        when(game.getLatestEnemyDamages()).thenReturn(new int[]{2, 5});

        int gameId = 7;
        ApiGameDTO dto = GameMapper.toApiGameDTO(game, gameId);

        assertEquals(7, dto.gameNumber);
        assertTrue(dto.isGameWon);
        assertFalse(dto.isGameLost);
        assertEquals(42, dto.opponentPoints);
        assertEquals(3L, dto.numActiveOpponentForts);
        assertArrayEquals(new int[]{2, 5}, dto.lastOpponentPoints);
    }

    @Test
    void toApiBoardDTO_defaultsToFog_whenNotShot_andCheatOff() {
        Game game = mock(Game.class);

        // Any cell, any coordinate -> cell is not shot and has no fort
        Cell cell = cell(false, false);
        when(game.getCellState(any(Coordinate.class))).thenReturn(cell);

        ApiBoardDTO dto = GameMapper.toApiBoardDTO(game, false);

        assertEquals(GameBoard.NUMBER_COLS, dto.boardWidth);
        assertEquals(GameBoard.NUMBER_ROWS, dto.boardHeight);
        assertEquals(GameBoard.NUMBER_ROWS, dto.cellStates.length);
        assertEquals(GameBoard.NUMBER_COLS, dto.cellStates[0].length);

        // Everything should be "fog" when cheat is off and nothing is shot
        for (int r = 0; r < dto.boardHeight; r++) {
            for (int c = 0; c < dto.boardWidth; c++) {
                assertEquals("fog", dto.cellStates[r][c], "Expected fog at (" + r + "," + c + ")");
            }
        }
    }

    @Test
    void toApiBoardDTO_setsMiss_whenShot_andNoFort() {
        Game game = mock(Game.class);

        // Any coordinate -> shot, no fort => "miss"
        Cell cell = cell(false, true);
        when(game.getCellState(any(Coordinate.class))).thenReturn(cell);

        ApiBoardDTO dto = GameMapper.toApiBoardDTO(game, false);

        assertEquals("miss", dto.cellStates[0][0]);
    }

    @Test
    void toApiBoardDTO_setsHit_whenShot_andHasFort() {
        Game game = mock(Game.class);

        // Any coordinate -> fort + shot => "hit"
        Cell cell = cell(true, true);
        when(game.getCellState(any(Coordinate.class))).thenReturn(cell);

        ApiBoardDTO dto = GameMapper.toApiBoardDTO(game, false);

        assertEquals("hit", dto.cellStates[0][0]);
    }

    @Test
    void toApiBoardDTO_setsFort_whenCheatOn_andHasFort_andNotShot() {
        Game game = mock(Game.class);

        // Any coordinate -> fort, not shot; with cheat => "fort"
        Cell cell = cell(true, false);
        when(game.getCellState(any(Coordinate.class))).thenReturn(cell);

        ApiBoardDTO dto = GameMapper.toApiBoardDTO(game, true);

        assertEquals("fort", dto.cellStates[0][0]);
    }

    @Test
    void toApiBoardDTO_setsField_whenCheatOn_andNoFort_andNotShot() {
        Game game = mock(Game.class);

        // Any coordinate -> no fort, not shot; with cheat => "field"
        Cell cell = cell(false, false);
        when(game.getCellState(any(Coordinate.class))).thenReturn(cell);

        ApiBoardDTO dto = GameMapper.toApiBoardDTO(game, true);

        assertEquals("field", dto.cellStates[0][0]);
    }

    @Test
    void toApiBoardDTO_usesCorrectCoordinateMapping() {
        Game game = mock(Game.class);

        // Make one special coordinate return a special cell; others return default cell.
        Cell defaultCell = cell(false, false); // would be "fog" if cheat off
        Cell specialCell = cell(false, true);  // would be "miss"

        when(game.getCellState(argThat(coordEq(2, 3)))).thenReturn(specialCell);
        when(game.getCellState(argThat(notCoordEq(2, 3)))).thenReturn(defaultCell);

        ApiBoardDTO dto = GameMapper.toApiBoardDTO(game, false);

        assertEquals("miss", dto.cellStates[2][3], "Expected miss at (2,3)");
        assertEquals("fog", dto.cellStates[0][0], "Expected fog at (0,0)");
    }

    // ---------- helpers ----------

    private static Cell cell(boolean hasFort, boolean hasBeenShot) {
        Cell cell = mock(Cell.class);
        when(cell.hasFort()).thenReturn(hasFort);
        when(cell.hasBeenShot()).thenReturn(hasBeenShot);
        return cell;
    }

    private static ArgumentMatcher<Coordinate> coordEq(int row, int col) {
        Coordinate expected = new Coordinate(row, col);
        return actual -> actual != null && actual.equals(expected);
    }

    private static ArgumentMatcher<Coordinate> notCoordEq(int row, int col) {
        Coordinate expected = new Coordinate(row, col);
        return actual -> actual == null || !actual.equals(expected);
    }
}
