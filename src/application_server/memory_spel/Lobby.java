package application_server.memory_spel;


import application_server.Utils.Utils;
import exceptions.GameNotCreatedException;
import exceptions.PlayerNumberexceededException;

import java.util.HashMap;
import java.util.Map;

import static application_server.Utils.Constants.*;

public class Lobby {
    private static Lobby lobby = null;
    private static Map<String, Game> activeGames; //gameId is key

    public static Lobby getLobby(){
        if(lobby == null)
            lobby = new Lobby();
        return lobby;
    }

    //singleton
    private Lobby(){
        activeGames = new HashMap<>();
    }

    //returned gameId
    public String createNewGame(int aantalSpelers, int bordGrootte) throws GameNotCreatedException {
        if(aantalSpelers <= MAX_PLAYER_COUNT && aantalSpelers >= MIN_PLAYER_COUNT && bordGrootte >= MIN_BOARD_SIZE && bordGrootte <= MAX_BOARD_SIZE) {
            String gameId = Utils.generateGameId();
            Game game = new Game(bordGrootte, gameId, aantalSpelers);
            activeGames.put(gameId, game);
            return gameId;
        }
        throw new GameNotCreatedException("aantal spelers/bordgrootte niet toegelaten.");
    }

    public static void deleteGame(String gameId){
        activeGames.remove(gameId);
    }

    public Map<String, Game> getActiveGames(){
        return activeGames;
    }


    public void joinGame(String gameId, Speler speler) throws PlayerNumberexceededException {
        Game game = activeGames.get(gameId);
        game.addSpeler(speler);
    }

    public Game getGame(String gameId){
        return activeGames.get(gameId);
    }
}
