package shared_client_appserver_stuff;

import application_server.memory_spel.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruben on 28/10/18.
 */
public class GameInfo {
    private String gameId;
    private int aantalSpelers;
    private List<String> spelers; //usernames van spelers
    private int spelersBeurt;
    private String creator;
    private String createDate;
    private boolean started;

    public GameInfo(Game game){
        this.gameId = game.getGameId();
        this.aantalSpelers = game.getAantalspelers();
        this.spelers = new ArrayList<>();
        game.getSpelers().forEach(e -> spelers.add(e.getUsername()));
        this.spelersBeurt = game.getSpelerbeurt();
        this.createDate = game.getCreateDate();
        this.creator = game.getCreator();
        this.started = game.isStarted();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getAantalSpelers() {
        return aantalSpelers;
    }

    public void setAantalSpelers(int aantalSpelers) {
        this.aantalSpelers = aantalSpelers;
    }

    public List<String> getSpelers() {
        return spelers;
    }

    public void setSpelers(List<String> spelers) {
        this.spelers = spelers;
    }

    public int getSpelersBeurt() {
        return spelersBeurt;
    }

    public void setSpelersBeurt(int spelersBeurt) {
        this.spelersBeurt = spelersBeurt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
