package ca.fortdefense.restapi;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the current state of a game session")
public class ApiGameDTO {

    @Schema(description = "Unique game number", example = "1")
    public int gameNumber;

    @Schema(description = "Indicates whether the game has been won")
    public boolean isGameWon;

    @Schema(description = "Indicates whether the game has been lost")
    public boolean isGameLost;

    @Schema(description = "Total points scored by the opponent", example = "12")
    public int opponentPoints;

    @Schema(description = "Number of opponent forts still active", example = "3")
    public long numActiveOpponentForts;

    @Schema(
            description = "Points scored by the opponent in their most recent turn. Empty if the opponent has not fired yet.",
            example = "[2, 3]"
    )
    public int[] lastOpponentPoints;

    public ApiGameDTO(
            int gameNumber,
            boolean isGameWon,
            boolean isGameLost,
            int opponentPoints,
            long numActiveOpponentForts,
            int[] lastOpponentPoints
    ) {
        this.gameNumber = gameNumber;
        this.isGameWon = isGameWon;
        this.isGameLost = isGameLost;
        this.opponentPoints = opponentPoints;
        this.numActiveOpponentForts = numActiveOpponentForts;
        this.lastOpponentPoints = lastOpponentPoints;
    }
}
