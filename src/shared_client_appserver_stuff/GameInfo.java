package shared_client_appserver_stuff;

import application_server.memory_spel.Game;
import application_server.memory_spel.Speler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruben on 28/10/18.
 */
public class GameInfo implements Serializable {
    private int gameId;
    private int aantalSpelers;
    private List<SpelerInfo> spelers;
    private int spelersBeurt;
    private String creator;
    private String createDate;
    private boolean started;
    private int breedte;
    private int lengte;
    private int thema;
    //private Map<SpelerInfo, Integer> puntentLijst;

    public GameInfo(Game game){
        //puntentLijst = new HashMap<>();
        this.gameId = game.getGameId();
        this.aantalSpelers = game.getAantalspelers();
        this.spelers = new ArrayList<>();
        game.getSpelers().forEach(e -> {
            SpelerInfo spelerInfo = new SpelerInfo(e, gameId);
            spelers.add(spelerInfo);
            //puntentLijst.put(spelerInfo, punten.get(e));
        });
        this.spelersBeurt = game.getSpelerbeurt();
        this.createDate = game.getCreateDate();
        this.creator = game.getCreator();
        this.started = game.isStarted();
        this.breedte = game.getBordspel().getBreedte();
        this.lengte = game.getBordspel().getLengte();
        this.thema = game.getBordspel().getType();
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getAantalSpelers() {
        return aantalSpelers;
    }

    public void setAantalSpelers(int aantalSpelers) {
        this.aantalSpelers = aantalSpelers;
    }

    public List<SpelerInfo> getSpelers() {
        return spelers;
    }

    public void setSpelers(List<SpelerInfo> spelers) {
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

    public int getBreedte() {
        return breedte;
    }

    public void setBreedte(int breedte) {
        this.breedte = breedte;
    }

    public int getLengte() {
        return lengte;
    }

    public void setLengte(int lengte) {
        this.lengte = lengte;
    }

    public int getThema() {
        return thema;
    }

    public void setThema(int thema) {
        this.thema = thema;
    }

   /* public Map<SpelerInfo, Integer> getPuntentLijst() {
        return puntentLijst;
    }

    public void setPuntentLijst(Map<SpelerInfo, Integer> puntentLijst) {
        this.puntentLijst = puntentLijst;
    }*/
}
