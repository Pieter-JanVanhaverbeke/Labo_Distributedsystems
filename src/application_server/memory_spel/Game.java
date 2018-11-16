package application_server.memory_spel;

import exceptions.GameAlreadyStartedException;
import exceptions.NotEnoughSpelersException;
import exceptions.NotYourTurnException;
import exceptions.PlayerNumberExceededException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static application_server.ServerImpl.impl;

/**
 * Created by ruben on 19/10/18.
 */
public class Game implements Serializable {

    private int gameId;
    private List<Speler> spelers;
    private Bordspel bordspel;
    private int spelerbeurt; //elke speler heeft index
    private int aantalspelers;
    private Map<Speler, Integer> puntenlijst;
    private String creator;
    private String createDate;
    private boolean started = false;
    private Kaart kaart1 = null;
    private Kaart kaart2 = null;
    private int theme;

    // SMALL = 4X4 MED = 6X6 LARGE = 8X8
    // Steeds 8 soorten
    // bordgrootte 1=small,2=medium,3=large
    public Game(int bordGrootte, int aantalspelers, String creator, int style) {
        this.creator = creator;
        this.createDate = ZonedDateTime.now(ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))).toString();
        this.aantalspelers = aantalspelers;
        this.spelers = new ArrayList<>();
        this.theme = style;
        int size = 2*bordGrootte+2;
        bordspel = new Bordspel(size, size);
        spelerbeurt = 0;
        puntenlijst = new HashMap<>();

        //iedere speler start met 0 punten
        for(Speler speler: spelers){
            puntenlijst.put(speler, 0);
        }
    }

    public Game(int bordGrootte, int aantalspelers, String creator, int style, int gameId) {
        this(bordGrootte, aantalspelers, creator, style);
        this.gameId = gameId;

    }

    public void addSpeler(Speler speler) throws PlayerNumberExceededException, RemoteException {
        if(spelers.size() < aantalspelers) {
            spelers.add(speler);
            puntenlijst.put(speler, 0);
            impl.addSpelerToGame(speler.getSpelerId(),gameId);

        }
        else
            throw new PlayerNumberExceededException("Het maximum aantal spelers voor deze game is bereikt. Kan speler niet meer toevoegen.");
    }

    public void removeSpeler(Speler speler) throws GameAlreadyStartedException, RemoteException {
        if(!started) {
            spelers.remove(speler);
            impl.removeSpelerToGame(speler.getSpelerId(), gameId);
        }
        else
            throw new GameAlreadyStartedException("De Game is al begonnen, je kan geen spelers meer verwijderen.");
    }

    //flip kaart, eerste kaart blijft geflipt tot 2e geflipt word.
    // If gelijk => punt bij speler tellen + kaarten blijven liggen,
    // if niet gelijk => kaarten draaien terug om. if spel is gedaan
    // (alle kaarten gedraaid) => punten worden aan spelers profiel toegevoegd
    public void flipCard(int x, int y, Speler speler) throws NotYourTurnException, NotEnoughSpelersException, RemoteException {
        started = true;
        //check of voldoende spelers zijn
        if(spelers.size() == aantalspelers) {
            //check of spelerbeurt ok is
            if (spelers.indexOf(speler) == spelerbeurt) {
                //check if eerste kaart al is omgedraaid
                if (kaart1 == null) {
                    //draai eerste kaart
                    kaart1 = bordspel.getBord()[x][y];
                    kaart1.draaiOm();
                } else {
                    //draai 2e kaart
                    kaart2 = bordspel.getBord()[x][y];
                    kaart2.draaiOm();

                    //check if soort van beide kaarten is gelijk
                    if (kaart1.getSoort() != kaart2.getSoort()) {
                        //draai terug alles om
                        kaart1.draaiOm();
                        kaart2.draaiOm();
                        kaart1 = kaart2 = null;
                    } else {
                        //verhoog punten van speler
                        puntenlijst.put(spelers.get(spelerbeurt), puntenlijst.get(spelerbeurt) + 1);  //speler die aan beurt is punt bijgeven
                        //check if game is gedaan, if so => schrijf punten naar spelersprofiel + delete game
                        if (bordspel.checkEindeSpel()) {
                            for (int i = 0; i < spelers.size(); i++) {
                                Speler speler2 = spelers.get(i);
                                speler2.increaseGlobalScore(puntenlijst.get(i));
                            }
                            Lobby.deleteGame(this.gameId);

                        }
                    }
                }
            }
            else
                throw new NotYourTurnException("U bent niet aan beurt.");
        }
        else
            throw new NotEnoughSpelersException("Er zijn te weinig spelers.");
    }

    /*public String toString(){
        StringBuilder result = new StringBuilder("Spelers: ");
        for(Speler speler: spelers)
            result.append(speler.getUsername() + " ");
        result.append("| " + spelers.get(getSpelerbeurt()).getUsername() + " is aan de beurt.");
        return result.toString();
    }*/

    public int getGameScore(Speler speler){
        return puntenlijst.get(speler);
    }

    public List<Speler> getSpelers() {
        return spelers;
    }

    public void setSpelers(List<Speler> spelers) {
        this.spelers = spelers;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Bordspel getBordspel() {
        return bordspel;
    }

    public void setBordspel(Bordspel bordspel) {
        this.bordspel = bordspel;
    }

    public int getSpelerbeurt() {
        return spelerbeurt;
    }

    public void setSpelerbeurt(int spelerbeurt) {
        this.spelerbeurt = spelerbeurt;
    }

    public int getAantalspelers() {
        return aantalspelers;
    }

    public void setAantalspelers(int aantalspelers) {
        this.aantalspelers = aantalspelers;
    }

    public Map<Speler, Integer> getPuntenlijst() {
        return puntenlijst;
    }

    public void setPuntenlijst(Map<Speler, Integer> puntenlijst) {
        this.puntenlijst = puntenlijst;
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

    //kan enkel maar op start zetten
    public void setStarted(boolean started) {
        if (started) {
            this.started = true;
        }
    }
}
