package shared_client_appserver_stuff;

import application_server.memory_spel.Game;
import application_server.memory_spel.Speler;

import java.io.Serializable;

/**
 * Created by ruben on 2/11/18.
 */
public class SpelerInfo implements Serializable {
    private String username;
    private int totalScore;
    private int gameScore;
    private String gameId;


    public SpelerInfo(Speler speler, String gameId, Game game) {
        this.gameId = gameId;
        this.totalScore = speler.getGlobalScore();
        this.username = speler.getUsername();
        this.gameScore = game.getGameScore(speler);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
