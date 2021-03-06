package memory_spel;

import java.util.List;

/**
 * Created by ruben on 19/10/18.
 */
public class Game {

    private String gameId;
    private Memoryspel memoryspel;
    private List<Speler> spelers;


    public Game(List<Speler> spelers, int bordGrootte, String gameId){
        memoryspel = new Memoryspel(spelers.size(), bordGrootte);
        this.spelers = spelers;
        this.gameId = gameId;
    }

    public void start(){
        List<Integer> scores = memoryspel.start();
        for(int i = 0; i<scores.size(); i++){
            spelers.get(i).increaseGlobalScore(scores.get(i));
        }
    }

    public String toString(){
        StringBuilder result = new StringBuilder("Spelers: ");
        for(Speler speler: spelers)
            result.append(speler.getUsername() + " ");
        result.append("| " + spelers.get(memoryspel.getSpelerbeurt()).getUsername() + " is aan de beurt.");
        return result.toString();
    }

    public Memoryspel getMemoryspel() {
        return memoryspel;
    }

    public void setMemoryspel(Memoryspel memoryspel) {
        this.memoryspel = memoryspel;
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

    public void addSpeler(Speler speler) {
        spelers.add(speler);
    }
}
