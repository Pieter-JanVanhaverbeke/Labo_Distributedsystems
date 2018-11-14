package application_server.memory_spel;
import application_server.Utils.Utils;
import exceptions.GameAlreadyStartedException;
import shared_client_appserver_stuff.GameInfo;
import exceptions.GameNotCreatedException;
import exceptions.PlayerNumberExceededException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static application_server.Utils.Constants.*;
import static application_server.ServerImpl.*;

public class Lobby implements Serializable{
    private static Map<Integer, Game> activeGames; //gameId is key

    //returned gameId
    public static int createNewGame(int aantalSpelers, int bordGrootte, String creator, int style) throws GameNotCreatedException, RemoteException {
        if(activeGames == null){
            activeGames = new HashMap<>();
        }

        if(aantalSpelers <= MAX_PLAYER_COUNT && aantalSpelers >= MIN_PLAYER_COUNT && bordGrootte >= MIN_BOARD_SIZE && bordGrootte <= MAX_BOARD_SIZE) {
            int gameId = 3;
            Game game = new Game(bordGrootte, gameId, aantalSpelers, creator);
            activeGames.put(gameId, game);

            String type = game.getBordspel().zetBordspelTypeOmNaarString();
            String faceup = game.getBordspel().zetBordspelOmNaarString();

            impl.createGame(creator,game.getCreateDate(),true,aantalSpelers,bordGrootte,style,type,faceup);
            return gameId;
        }
        throw new GameNotCreatedException("aantal spelers/bordgrootte niet toegelaten.");
    }

    public static void deleteGame(int gameId){
        dbUpdateGames();
        activeGames.remove(gameId);
        try {
            impl.deleteGame(gameId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Game> getActiveGames(){
        dbUpdateGames();
        return activeGames;
    }

    public static List<GameInfo> getActiveGamesList() {
        //eerst nieuwe info uit db halen
        dbUpdateGames();
        List<GameInfo> result = new ArrayList<>();

        if(activeGames != null)
            for(Integer key: activeGames.keySet()){
                Game game = activeGames.get(key);
                result.add(new GameInfo(game));
            }

        return result;
    }

    public static void joinGame(int gameId, Speler speler) throws PlayerNumberExceededException, RemoteException {
        Game game = activeGames.get(gameId);
        game.addSpeler(speler);
    }

    public static void unJoinGame(int gameId, Speler speler) throws GameAlreadyStartedException, RemoteException {
        Game game = activeGames.get(gameId);
        game.removeSpeler(speler);
    }

    public static Game getGame(int gameId){
        //eerst nieuwe info uit db halen
        dbUpdateGames();
        return activeGames.get(gameId);
    }

    private static void dbUpdateGames(){
        try {
            activeGames = impl.getAllGames();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
