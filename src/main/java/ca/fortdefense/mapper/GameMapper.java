package ca.fortdefense.mapper;

import ca.fortdefense.model.Cell;
import ca.fortdefense.model.Coordinate;
import ca.fortdefense.model.Game;
import ca.fortdefense.model.GameBoard;
import ca.fortdefense.restapi.ApiBoardDTO;
import ca.fortdefense.restapi.ApiGameDTO;

public class GameMapper {

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
        return new ApiBoardDTO(boardWidth,
                boardHeight,
                cellStates);
    }
}
