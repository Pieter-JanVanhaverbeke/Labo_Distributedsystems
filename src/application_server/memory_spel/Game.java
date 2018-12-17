package application_server.memory_spel;

import exceptions.*;
import shared_client_appserver_stuff.GameInfo;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static application_server.ServerMain.*;

/**
 * Created by ruben on 19/10/18.
 */
public class Game implements Serializable {

    private String gameId;
    private List<Speler> spelers;
    private Bordspel bordspel;
    private int spelerbeurt; //elke speler heeft index
    private int aantalspelers;
    private Map<Integer, Integer> puntenlijst;
    private String creator;
    private String createDate;
    private boolean started = false;
    private Kaart kaart1 = null;
    private Kaart kaart2 = null;
    private int theme;
    private Speler huidigespeler = null;
    //server waarop deze game gespeeld word
    private ServerInfo serverInfo;
    private int counter = 0;
    private int BORD_DB_UPDATE_INTERVAL = 3;

    private int tmpX;
    private int tmpY;


    // SMALL = 4X4 MED = 6X6 LARGE = 8X8
    // Steeds 8 soorten
    // bordgrootte 1=small,2=medium,3=large
    public Game(int bordGrootte, int aantalspelers, String creator, int style) {
        this.creator = creator;
        this.createDate = ZonedDateTime.now(ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))).toString();
        this.aantalspelers = aantalspelers;
        this.spelers = new ArrayList<>();
        this.theme = style;
        int size = 2 * bordGrootte + 2;
        bordspel = new Bordspel(size, size);
        bordspel.setType(style);
        spelerbeurt = 0;
        puntenlijst = new HashMap<>();
    }

    public Game(int bordGrootte, int aantalspelers, String creator, int style, String gameId) {
        this(bordGrootte, aantalspelers, creator, style);
        this.gameId = gameId;
    }

    public void addSpeler(Speler speler) throws PlayerNumberExceededException, RemoteException, NoServerAvailableException {

        try {
            if (spelers.size() < aantalspelers) {
                spelers.add(speler);
                puntenlijst.put(speler.getSpelerId(), 0);
                dbImpl.addSpelerToGame(speler.getSpelerId(), gameId);
            } else
                throw new PlayerNumberExceededException("Het maximum aantal spelers voor deze game is bereikt. Kan speler niet meer toevoegen.");

        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            addSpeler(speler);
        }

    }

    public void removeSpeler(Speler speler) throws GameAlreadyStartedException, RemoteException, NoServerAvailableException {
        try {
            if (!started) {
                spelers.removeIf(e -> e.getSpelerId() == speler.getSpelerId());
                dbImpl.removeSpelerToGame(speler.getSpelerId(), gameId);
            } else
                throw new GameAlreadyStartedException("De Game is al begonnen, je kan geen spelers meer verwijderen.");
        } catch (ConnectException re) {
            re.printStackTrace();
            renewDbServer();
            removeSpeler(speler);
        }
    }


    //flip kaart, eerste kaart blijft geflipt tot 2e geflipt word.
    // If gelijk => punt bij speler tellen + kaarten blijven liggen,
    // if niet gelijk => kaarten draaien terug om. if spel is gedaan
    // (alle kaarten gedraaid) => punten worden aan spelers profiel toegevoegd
    public boolean flipCard(int x, int y, Speler speler) throws NotYourTurnException, NotEnoughSpelersException, RemoteException, NoServerAvailableException {
        try {
            started = true;
            boolean firstFlip = false;
            huidigespeler = spelers.get(spelerbeurt);

            //check of voldoende spelers zijn
            if (spelers.size() != aantalspelers) {
                throw new NotEnoughSpelersException("Er zijn te weinig spelers.");
            }

            //get speler uit spelerslijst
            List<Speler> s = spelers.stream().filter(e -> e.getSpelerId() == speler.getSpelerId()).collect(Collectors.toList());
            if (s.size() != 1) //if verkeert => logic is verkeert => doe niets??
                return firstFlip;


            //check of spelerbeurt ok is
            //   if (spelers.indexOf(s.get(0)) != spelerbeurt){
            if (huidigespeler.getSpelerId() != speler.getSpelerId()) {
                throw new NotYourTurnException("U bent niet aan beurt.");
            }

            //alles ok => kaart draaien
            if (bordspel.getBordRemote()[x][y] == -1) {
                //check if eerste kaart al is omgedraaid
                if (kaart1 == null) {
                    //draai eerste kaart
                    kaart1 = bordspel.getBord()[x][y];
                    kaart1.draaiOm();
                    firstFlip = true;
                    tmpX = x;
                    tmpY = y;
                } else {
                    //draai 2e kaart
                    kaart2 = bordspel.getBord()[x][y];
                    kaart2.draaiOm();
                    //TODO CLIENTS EVEN 2de KAART LATEN TONEN

                    //check if soort van beide kaarten is gelijk
                    if (kaart1.getSoort() != kaart2.getSoort()) {
                        //draai terug alles om
                        kaart1.draaiOm();
                        kaart2.draaiOm();
                        kaart1 = null;
                        kaart2 = null;
                        spelerbeurt = (spelerbeurt + 1) % aantalspelers;
                        //dbImpl.updateSpelersbeurt(gameId, spelerbeurt);
                    } else {
                        //speler die aan beurt is punt bijgeven
                        int spelerId = spelers.get(spelerbeurt).getSpelerId();
                        puntenlijst.merge(spelerId, 1, Integer::sum);
                        counter++;
                        //check if game is gedaan, if so => schrijf punten naar spelersprofiel + delete game
                        if (bordspel.checkEindeSpel()) {
                            for (int i = 0; i < spelers.size(); i++) {
                                Speler speler2 = spelers.get(i);
                                int spelerid2 = speler2.getSpelerId();
                                speler2.increaseGlobalScore(puntenlijst.get(spelerid2));
                                schrijfNaarDB();
                                dbImpl.updateGlobalScore(spelerid2, puntenlijst.get(spelerid2));   //UPDATEN PUNTEN DB
                            }

                        }
                        if(counter > BORD_DB_UPDATE_INTERVAL) {
                            schrijfNaarDB();
                            counter = 0;
                        }
                        kaart1 = null;
                        kaart2 = null;
                    }
                }
            }

            updateBoardClients();
            return firstFlip;
        }
        catch (ConnectException re){
            re.printStackTrace();
            renewDbServer();
            return flipCard(x, y, speler);
        }
    }

    public void schrijfNaarDB() throws RemoteException, NoServerAvailableException {
        try {
            //pas in db zetten als paar echt gevonden is
            //DATABANK

            /*String faceup = dbImpl.getFaceUp(gameId);

            //LOGICA OM UP TE DATEN NAAR DATABENK  TODO mss andere plaats zetten
            int bordlengte = (int) Math.sqrt(faceup.length() / 2);                //bordspelsize halen uit lengte string.
            int coordinaat1 = 2 * bordlengte * x + 2 * y;           //coordinaat dat moet vervangen worden in string.
            int coordinaat2 = 2 * bordlengte * tmpX + 2 * tmpY;           //coordinaat dat moet vervangen worden in string.
            char face = '0';

            StringBuilder sb = new StringBuilder(faceup);

            if (faceup.charAt(coordinaat1) == face) {
                sb.setCharAt(coordinaat1, '1');
            }
            if (faceup.charAt(coordinaat2) == face) {
                sb.setCharAt(coordinaat2, '1');
            }

            faceup = sb.toString();
            */


            dbImpl.updateFaceUp(gameId, getFaceUp());

            /*//Schrijven punten naar DB
            for (int i = 0; i < aantalspelers; i++) {
                int spelerid = spelers.get(i).getSpelerId();
                dbImpl.updatePunten(gameId, spelerid, puntenlijst.get(spelerid));
            }*/
        }
        catch (ConnectException re){
            re.printStackTrace();
            renewDbServer();
            schrijfNaarDB();
        }

    }

    public String getFaceUp(){
        StringBuilder faceup = new StringBuilder();

        Kaart[][] bord = bordspel.getBord();
        String s = "";
        for(int i = 0; i<bord.length; i++){
            for (int j = 0; j<bord[0].length; j++){
                Kaart kaart = bord[i][j];
                if(kaart.isFaceUp())
                    faceup.append(s + "1");
                else{
                    faceup.append(s + "0");
                }
                s = " ";
            }
        }
        return faceup.toString();
    }

    private void updateBoardClients() {
        try {
            GameInfo gameInfo = new GameInfo(this);
            for(String username: gameUpdateSubscribers.get(gameId))
                clients.get(username).updateBord(gameInfo);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getGameScore(Speler speler){
        return puntenlijst.get(speler.getSpelerId());
    }

    public List<Speler> getSpelers() {
        return spelers;
    }

    public void setSpelers(List<Speler> spelers) {
        this.spelers = spelers;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
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

    public Map<Integer, Integer> getPuntenlijst() {
        return puntenlijst;
    }

    public void setPuntenlijst(Map<Integer, Integer> puntenlijst) {
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
            this.started = started;
        }

    public Speler getHuidigespeler() {
        return huidigespeler;
    }

    public void setHuidigespeler(Speler huidigespeler) {
        this.huidigespeler = huidigespeler;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}

