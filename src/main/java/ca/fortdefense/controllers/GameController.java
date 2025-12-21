package ca.fortdefense.controllers;

import ca.fortdefense.exception.BadInputException;
import ca.fortdefense.exception.IdNotFoundException;
import ca.fortdefense.mapper.GameMapper;
import ca.fortdefense.model.Coordinate;
import ca.fortdefense.model.Game;
import ca.fortdefense.restapi.ApiBoardDTO;
import ca.fortdefense.restapi.ApiGameDTO;
import ca.fortdefense.restapi.ApiLocationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class GameController {
    /*
    need controllers for:
    get /api/games/ + appObj
    get /api/games/ + appObj + /moves
     */
    private final List<Game> games = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger();
    private boolean cheatMode;

    @GetMapping("/api/about")
    public String authorName() {
        return "Arun Paudel";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/games")
    public ApiGameDTO makeNewGame() {
        Game newGame = new Game(5);
        games.add(newGame);
        cheatMode = false;
        return GameMapper.toApiGameDTO(newGame, nextId.incrementAndGet());
    }

    @GetMapping("/api/games/{gameId}")
    public ApiGameDTO loadGame(@PathVariable("gameId") int gameId) {
        try {
            return GameMapper.toApiGameDTO(games.get(gameId - 1), nextId.get());
        } catch (IndexOutOfBoundsException e) {
            throw new IdNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/api/games/{gameId}/board")
    public ApiBoardDTO makeNewBoard(@PathVariable("gameId") int gameId) {
        try {
            return GameMapper.toApiBoardDTO(games.get(gameId - 1), cheatMode);
        } catch (IndexOutOfBoundsException e) {
            throw new IdNotFoundException(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/api/games/{gameId}/cheatstate")
    public void cheatStateBoard(@PathVariable("gameId") int gameId,
                                @RequestBody String cheatType) {
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
    public void cellAttacked(@PathVariable("gameId") int gameId,
                             @RequestBody ApiLocationDTO inputCell) {
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

    @ResponseStatus(value = HttpStatus.NOT_FOUND,
            reason = "Request ID not found.")
    @ExceptionHandler(IdNotFoundException.class)
    public void badIdExceptionHandler() {
        //Nothing to do
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST,
            reason = "Request input is not valid.")
    @ExceptionHandler(BadInputException.class)
    public void badRequestExceptionHandler() {
        //Nothing to do
    }

}
