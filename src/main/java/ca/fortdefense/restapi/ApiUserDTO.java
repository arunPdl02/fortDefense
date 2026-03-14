package ca.fortdefense.restapi;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the user's info")
public class ApiUserDTO {

    public String userName;

    @Schema(description = "Total number of games won by this user", example = "3")
    public int winCount;

    @Schema(description = "Total number of games loss by this user", example = "3")
    public int lossCount;

    public ApiUserDTO(String userName, int winCount, int lossCount) {
        this.userName = userName;
        this.winCount = winCount;
        this.lossCount = lossCount;
    }
}
