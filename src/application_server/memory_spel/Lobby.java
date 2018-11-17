package application_server.memory_spel;
import application_server.Utils.Utils;
import exceptions.*;
import shared_client_appserver_stuff.GameInfo;

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
            Game game = new Game(bordGrootte, aantalSpelers, creator, style);

            String type = game.getBordspel().zetBordspelTypeOmNaarString();
            String faceup = game.getBordspel().zetBordspelOmNaarString();

            int gameId = impl.createGame(creator, game.getCreateDate(),false,game.getAantalspelers(),bordGrootte,style,type,faceup);
            game.setGameId(gameId);
            activeGames.put(gameId, game);

            return gameId;
        }
        throw new GameNotCreatedException("aantal spelers/bordgrootte niet toegelaten.");
    }

    public static void deleteGame(int gameId){
        try {
            impl.deleteGame(gameId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        dbUpdateGames();
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
        //dbUpdateGames(); //TODO
        return activeGames.get(gameId);
    }

    private static void dbUpdateGames(){
        try {
            activeGames = impl.getAllGames();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Game flipCard(int gameId, int x, int y, Speler speler) throws NotYourTurnException, NotEnoughSpelersException, RemoteException {
        //omdat enkel na gevonden paar pas in db komt => activeGames niet updates vanuit db zolang 2e kaart niet gedraaid is
        Game game = activeGames.get(gameId);

        if(!game.flipCard(x, y, speler))
            dbUpdateGames();
        return game;

    }
}
