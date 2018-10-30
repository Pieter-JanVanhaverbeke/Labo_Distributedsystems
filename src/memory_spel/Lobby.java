package memory_spel;


import Utils.Utils;
import exceptions.GameNotCreatedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static memory_spel.Constants.*;

public class Lobby {
    private Map<String, Game> activeGames;

    public Lobby(){
        activeGames = new HashMap<>();
    }

    //return true if game successful gemaakt, false if not
    public synchronized String createNewGame(int aantalSpelers, int bordGrootte) throws GameNotCreatedException {
        if(aantalSpelers <= MAX_PLAYER_COUNT && aantalSpelers >= MIN_PLAYER_COUNT && bordGrootte >= MIN_BOARD_SIZE && bordGrootte <= MAX_BOARD_SIZE) {
            String gameId = Utils.generateGameId();
            List<Speler> spelers = new ArrayList<>();
            Game game = new Game(spelers, bordGrootte, gameId);
            activeGames.put(gameId, game);
            return gameId;
        }

        throw new GameNotCreatedException("aantal spelers/bordgrootte niet toegelaten.");
    }

    public synchronized void deleteGame(Game game){
        activeGames.remove(game);
    }

    public Map<String, Game> getActiveGames(){
        return activeGames;
    }


    public void joinGame(String gameId, Speler speler) {
        Game game = activeGames.get(gameId);
        game.addSpeler(speler);
    }
}
