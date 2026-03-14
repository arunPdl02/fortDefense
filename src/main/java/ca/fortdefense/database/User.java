package ca.fortdefense.database;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String userName;
    private int gameWon;
    private int gameLoss;

    public User(String userName, int gameWon, int gameLoss) {
        this.userName = userName;
        this.gameWon = gameWon;
        this.gameLoss = gameLoss;
    }

    public String getUserName() {
        return userName;
    }
    public int getGameWon() {
        return gameWon;
    }

    public int getGameLoss() {
        return gameLoss;
    }

    public void incrementGameWon() {
        this.gameWon++;
    }

    public void incrementGameLoss() {
        this.gameLoss++;
    }
}
