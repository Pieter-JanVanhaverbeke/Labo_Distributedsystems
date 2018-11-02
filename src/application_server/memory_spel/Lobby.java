package application_server.memory_spel;


import application_server.Utils.Utils;
import exceptions.GameAlreadyStartedException;
import shared_client_appserver_stuff.GameInfo;
import exceptions.GameNotCreatedException;
import exceptions.PlayerNumberExceededException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static application_server.Utils.Constants.*;

public class Lobby {
    private static Map<String, Game> activeGames; //gameId is key


    //singleton
    public Lobby(){
        activeGames = new HashMap<>();
    }

    //returned gameId
    public String createNewGame(int aantalSpelers, int bordGrootte, String creator) throws GameNotCreatedException {
        if(aantalSpelers <= MAX_PLAYER_COUNT && aantalSpelers >= MIN_PLAYER_COUNT && bordGrootte >= MIN_BOARD_SIZE && bordGrootte <= MAX_BOARD_SIZE) {
            String gameId = Utils.generateGameId();
            Game game = new Game(bordGrootte, gameId, aantalSpelers, creator);
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

    public List<GameInfo> getActiveGamesList() {
        List<GameInfo> result = new ArrayList<>();

        for(String key: activeGames.keySet()){
            Game game = activeGames.get(key);
            result.add(new GameInfo(game));
        }
        return result;
    }

    public void joinGame(String gameId, Speler speler) throws PlayerNumberExceededException {
        Game game = activeGames.get(gameId);
        game.addSpeler(speler);
    }

    public void unJoinGame(String gameId, Speler speler) throws GameAlreadyStartedException {
        Game game = activeGames.get(gameId);
        game.removeSpeler(speler);
    }

    public Game getGame(String gameId){
        return activeGames.get(gameId);
    }
}
