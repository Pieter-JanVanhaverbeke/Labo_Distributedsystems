package memory_spel;


import java.util.ArrayList;
import java.util.List;

import static memory_spel.Constants.*;

public class Lobby {
    private List<Game> activeGames;


    public Lobby(){
        activeGames = new ArrayList<>();
    }

    //return true if gam successful gemaakt, false if not
    public synchronized boolean createNewGame(List<Speler> spelers, int bordGrootte){
        int aantalSpelers = spelers.size();
        if(aantalSpelers <= MAX_PLAYER_COUNT && aantalSpelers >= MIN_PLAYER_COUNT && bordGrootte >= MIN_BOARD_SIZE && bordGrootte <= MAX_BOARD_SIZE) {
            Game game = new Game(spelers, bordGrootte);
            activeGames.add(game);
            return true;
        }
        else
            return false;
    }

    public synchronized void deleteGame(Game game){
        activeGames.remove(game);
    }

    public List<Game> getActiveGames(){
        return activeGames;
    }




}
