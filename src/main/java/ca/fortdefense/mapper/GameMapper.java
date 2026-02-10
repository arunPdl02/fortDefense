package ca.fortdefense.mapper;

import ca.fortdefense.model.Cell;
import ca.fortdefense.model.Coordinate;
import ca.fortdefense.model.Game;
import ca.fortdefense.model.GameBoard;
import ca.fortdefense.restapi.ApiBoardDTO;
import ca.fortdefense.restapi.ApiGameDTO;

/**
 * Maps internal domain model objects to API-facing DTOs.
 *
 * <p>This class isolates transformation logic between the core game model and
 * the REST API layer, ensuring that internal representations are not exposed
 * directly to API consumers.</p>
 *
 * <p>All methods are stateless and side-effect free.</p>
 */
public class GameMapper {

    /**
     * Converts a {@link Game} domain object into an {@link ApiGameDTO}
     * suitable for REST API responses.
     *
     * @param game   the game domain object
     * @param gameId the externally visible game identifier
     * @return an {@link ApiGameDTO} representing the current game state
     */
    public static ApiGameDTO toApiGameDTO(Game game, int gameId) {

        return new ApiGameDTO(
                gameId,
                game.hasUserWon(),
                game.hasUserLost(),
                game.getEnemyPoints(),
                game.getNumberOfActiveEnemies(),
                game.getLatestEnemyDamages()
        );
    }

    /**
     * Converts a {@link Game} domain object into an {@link ApiBoardDTO}
     * representing the current board state.
     *
     * <p>The output varies depending on whether cheat mode is enabled.
     * When enabled, additional information about unshot cells is exposed.</p>
     *
     * @param game      the game domain object
     * @param cheatMode whether cheat mode is enabled
     * @return an {@link ApiBoardDTO} representing the board state
     */
    public static ApiBoardDTO toApiBoardDTO(Game game, boolean cheatMode) {
        int boardWidth = GameBoard.NUMBER_COLS;
        int boardHeight = GameBoard.NUMBER_ROWS;
        String[][] cellStates = new String[boardHeight][boardWidth];

        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                Cell cell = game.getCellState(new Coordinate(i, j));
                cellStates[i][j] = "fog";

                if (cell.hasFort()) {
                    if (cell.hasBeenShot()) {
                        cellStates[i][j] = "hit";
                    } else if (cheatMode) {
                        cellStates[i][j] = "fort";
                    }
                } else if (cell.hasBeenShot()) {
                    cellStates[i][j] = "miss";
                } else if (cheatMode) {
                    cellStates[i][j] = "field";
                }
            }
        }

        return new ApiBoardDTO(boardWidth, boardHeight, cellStates);
    }
}
