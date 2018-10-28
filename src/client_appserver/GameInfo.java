package client_appserver;

import application_server.memory_spel.Game;
import application_server.memory_spel.Speler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruben on 28/10/18.
 */
public class GameInfo {
    private String gameId;
    private int aantalSpelers;
    private List<String> spelers;
    private int spelersBeurt;
    private String creator;
    private String createDate;

    public GameInfo(Game game){
        this.gameId = game.getGameId();
        this.aantalSpelers = game.getAantalspelers();
        this.spelers = new ArrayList<>();
        game.getSpelers().forEach(e -> spelers.add(e.getUsername()));
        this.spelersBeurt = game.getSpelerbeurt();
        this.createDate = game.getCreateDate();
        this.creator = game.getCreator();
    }
}
