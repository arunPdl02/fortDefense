package ca.fortdefense.restapi;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a board coordinate selected by the player")
public class ApiLocationDTO {

    @Schema(description = "Row index on the board (0-based)", example = "2")
    public int row;

    @Schema(description = "Column index on the board (0-based)", example = "3")
    public int col;
}
