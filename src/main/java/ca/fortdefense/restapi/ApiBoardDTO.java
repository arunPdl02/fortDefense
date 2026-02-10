package ca.fortdefense.restapi;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the visible state of the game board")
public class ApiBoardDTO {

    @Schema(description = "Width of the board", example = "5")
    public int boardWidth;

    @Schema(description = "Height of the board", example = "5")
    public int boardHeight;

    @Schema(
            description = "2D array representing cell states on the board. Possible values: fog, hit, fort, miss, field",
            example = "[[\"fog\",\"fog\"],[\"miss\",\"hit\"]]"
    )
    public String[][] cellStates;

    public ApiBoardDTO(int boardWidth, int boardHeight, String[][] cellStates) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.cellStates = cellStates;
    }
}
