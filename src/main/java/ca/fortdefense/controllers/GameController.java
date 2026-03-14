package ca.fortdefense.controllers;

import ca.fortdefense.database.User;
import ca.fortdefense.database.UserRepository;
import ca.fortdefense.exception.BadInputException;
import ca.fortdefense.exception.IdNotFoundException;
import ca.fortdefense.mapper.GameMapper;
import ca.fortdefense.model.Coordinate;
import ca.fortdefense.model.Game;
import ca.fortdefense.restapi.ApiBoardDTO;
import ca.fortdefense.restapi.ApiGameDTO;
import ca.fortdefense.restapi.ApiLocationDTO;
import ca.fortdefense.restapi.ApiUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Tag(name = "Games", description = "Endpoints for creating, loading, and playing Fort Defense games")
public class GameController {
    private final List<Game> games = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger();
    private boolean cheatMode;

    @Autowired
    private UserRepository repository;

    @GetMapping("/api/about")
    @Operation(summary = "Get author info", description = "Returns a simple string identifying the author of the service.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author info returned")
    })
    public String authorName() {
        return "Arun Paudel";
    }

    @GetMapping("/api/user")
    public ApiUserDTO userStatus(@RequestParam("name") String userName){
        User u = repository.findByUserName(userName);
        if (u == null){
            User newUser = new User(userName, 0, 0);
            repository.save(newUser);
            return GameMapper.toApiUserDTO(newUser);
        }
        return GameMapper.toApiUserDTO(u);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/games")
    @Operation(summary = "Create a new game", description = "Creates a new game instance with default settings and returns the game metadata.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game created",
                    content = @Content(schema = @Schema(implementation = ApiGameDTO.class)))
    })
    public ApiGameDTO makeNewGame() {
        System.out.println("MONGO_PASSWORD=" + System.getenv("MONGO_PASSWORD"));
        Game newGame = new Game(5);
        games.add(newGame);
        cheatMode = false;
        return GameMapper.toApiGameDTO(newGame, nextId.incrementAndGet());
    }

    @GetMapping("/api/games/{gameId}")
    @Operation(summary = "Load a game", description = "Loads a previously created game by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Game loaded",
                    content = @Content(schema = @Schema(implementation = ApiGameDTO.class))),
            @ApiResponse(responseCode = "404", description = "Game ID not found")
    })
    public ApiGameDTO loadGame(
            @Parameter(description = "Game ID (1-based)", example = "1", required = true)
            @PathVariable("gameId") int gameId
    ) {
        try {
            return GameMapper.toApiGameDTO(games.get(gameId - 1), nextId.get());
        } catch (IndexOutOfBoundsException e) {
            throw new IdNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/api/games/{gameId}/board")
    @Operation(summary = "Get game board", description = "Returns the current board view for the game. Board output may change based on cheat state.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Board returned",
                    content = @Content(schema = @Schema(implementation = ApiBoardDTO.class))),
            @ApiResponse(responseCode = "404", description = "Game ID not found")
    })
    public ApiBoardDTO makeNewBoard(
            @Parameter(description = "Game ID (1-based)", example = "1", required = true)
            @PathVariable("gameId") int gameId
    ) {
        try {
            return GameMapper.toApiBoardDTO(games.get(gameId - 1), cheatMode);
        } catch (IndexOutOfBoundsException e) {
            throw new IdNotFoundException(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/api/games/{gameId}/cheatstate")
    @Operation(summary = "Set cheat mode", description = "Enables cheat mode when provided the supported cheat code.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Cheat state updated"),
            @ApiResponse(responseCode = "400", description = "Invalid cheat code"),
            @ApiResponse(responseCode = "404", description = "Game ID not found")
    })
    public void cheatStateBoard(
            @Parameter(description = "Game ID (1-based)", example = "1", required = true)
            @PathVariable("gameId") int gameId,
            @RequestBody(
                    description = "Cheat code. Currently supported: SHOW_ALL",
                    required = true,
                    content = @Content(schema = @Schema(type = "string", example = "SHOW_ALL"))
            )
            @org.springframework.web.bind.annotation.RequestBody String cheatType
    ) {
        if (gameId >= 1 && gameId <= games.size()) {
            if (cheatType.equals("SHOW_ALL")) {
                cheatMode = true;
            } else {
                throw new BadInputException("Wrong cheat code, try again!");
            }
        } else {
            throw new IdNotFoundException("Invalid game id, try again!");
        }
    }

    @PostMapping("/api/games/{gameId}/moves")
    @Operation(summary = "Make a move", description = "Records a player's shot at a board location and triggers enemy shots.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Move processed"),
            @ApiResponse(responseCode = "400", description = "Invalid move input"),
            @ApiResponse(responseCode = "404", description = "Game ID not found")
    })
    public void cellAttacked(
            @Parameter(description = "Game ID (1-based)", example = "1", required = true)
            @PathVariable("gameId") int gameId,
            @RequestBody(
                    description = "Target cell location (row, col).",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ApiLocationDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ApiLocationDTO inputCell
    ) {
        try {
            Coordinate cell = new Coordinate(inputCell.row, inputCell.col);
            cell.rowAndColAreValid(inputCell.row, inputCell.col);
            Game currentGame = games.get(gameId - 1);
            currentGame.recordPlayerShot(cell);
            currentGame.fireEnemyShots();
        } catch (IndexOutOfBoundsException e) {
            throw new IdNotFoundException(e.getMessage());
        } catch (InvalidParameterException e) {
            throw new BadInputException(e.getMessage());
        }
    }

    @PostMapping("/api/game/update/loss")
    public ApiUserDTO incrementLoss(@RequestParam("name") String userName){
        User u = repository.findByUserName(userName);
        u.incrementGameLoss();
        repository.save(u);
        return GameMapper.toApiUserDTO(u);
    }

    @PostMapping("/api/games/update/win")
    public ApiUserDTO incrementWin(@RequestParam("name") String userName){
        User u = repository.findByUserName(userName);
        u.incrementGameWon();
        repository.save(u);
        return GameMapper.toApiUserDTO(u);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Request ID not found.")
    @ExceptionHandler(IdNotFoundException.class)
    public void badIdExceptionHandler() {
        // Nothing to do
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request input is not valid.")
    @ExceptionHandler(BadInputException.class)
    public void badRequestExceptionHandler() {
        // Nothing to do
    }
}
