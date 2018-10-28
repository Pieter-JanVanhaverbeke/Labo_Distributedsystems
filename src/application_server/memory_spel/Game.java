package application_server.memory_spel;

import exceptions.NotEnoughSpelersException;
import exceptions.NotYourTurnException;
import exceptions.PlayerNumberexceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruben on 19/10/18.
 */
public class Game {

    private String gameId;
    private List<Speler> spelers;
    private Bordspel bordspel;
    private  int spelerbeurt; //elke speler heeft index
    private int aantalspelers;
    private List<Integer> puntenlijst;
    private Kaart kaart1 = null;
    private Kaart kaart2 = null;

    // SMALL = 4X4 MED = 6X6 LARGE = 8X8
    // Steeds 8 soorten
    // bordgrootte 1=small,2=medium,3=large
    public Game(int bordGrootte, String gameId, int aantalspelers){
        this.aantalspelers = aantalspelers;
        this.spelers = new ArrayList<>();
        this.gameId = gameId;
        int size = 2*bordGrootte+2;
        bordspel = new Bordspel(size, size);
        spelerbeurt = 0;
        puntenlijst = new ArrayList<>();

        //iedere speler start met 0 punten
        for(int i=0; i<aantalspelers;i++){
            puntenlijst.add(0);
        }
    }

    public void addSpeler(Speler speler) throws PlayerNumberexceededException {
        if(spelers.size() < aantalspelers)
            spelers.add(speler);
        else
            throw new PlayerNumberexceededException("Het maximum aantal spelers voor deze game is bereikt. Kan speler niet meer toevoegen.");
    }

    //flip kaart, eerste kaart blijft geflipt tot 2e geflipt word.
    // If gelijk => punt bij speler tellen + kaarten blijven liggen,
    // if niet gelijk => kaarten draaien terug om. if spel is gedaan
    // (alle kaarten gedraaid) => punten worden aan spelers profiel toegevoegd
    public void flipCard(int x, int y, Speler speler) throws NotYourTurnException, NotEnoughSpelersException {
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
                        puntenlijst.set(spelerbeurt, puntenlijst.get(spelerbeurt) + 1);  //speler die aan beurt is punt bijgeven
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
        else{
            throw new NotEnoughSpelersException("Er zijn te weinig spelers.");
        }
    }

    public String toString(){
        StringBuilder result = new StringBuilder("Spelers: ");
        for(Speler speler: spelers)
            result.append(speler.getUsername() + " ");
        result.append("| " + spelers.get(getSpelerbeurt()).getUsername() + " is aan de beurt.");
        return result.toString();
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

    public List<Integer> getPuntenlijst() {
        return puntenlijst;
    }

    public void setPuntenlijst(List<Integer> puntenlijst) {
        this.puntenlijst = puntenlijst;
    }

}
