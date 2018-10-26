package application_server;

import DbConnection.dbConnection;
import Utils.Utils;
import exceptions.*;
import memory_spel.Game;
import memory_spel.Lobby;
import memory_spel.Speler;
import rmi_int_client_appserver.rmi_int_client_appserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver {
    private HashMap<String, Speler> userTokens = new HashMap<>(); //bevat de huidig uitgeleende tokens ( = aangemelde users)

    private Lobby lobby;

    public ServerImpl() throws RemoteException {
        lobby = Lobby.getLobby();
    }

    //////////////////////////////// Control //////////////////////////////////////////
    @Override
    public String RegistrerNewClient(String username, String passwdHash) throws UsernameAlreadyInUseException {
        if(dbConnection.getUserSet().contains(username)){
            System.out.println("gebruikersnaam al gebruikt");
            throw new UsernameAlreadyInUseException();
        }

        String token = Utils.generateUserToken(username);
        Speler speler = new Speler(username);
        userTokens.put(token, speler);
        //dbConnection.insert(username,passwdHash); //TODO: db
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        return token;

   }

    @Override
    public String logIn(String username, String password) {
        String token = Utils.generateUserToken(username);
        Speler speler = null; //TODO: = get speler op username uit db
        userTokens.put(token, speler);
        return token;
    }

    @Override
    public void logOut(String token) {
        Utils.invalidateToken(token);
        userTokens.remove(token);
    }

    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    @Override
    public String createGame(int aantalSpelers, int bordGrootte, String token) throws GameNotCreatedException, NoValidTokenException {
        if(validateToken(token))
            return lobby.createNewGame(aantalSpelers, bordGrootte);
        throw new NoValidTokenException("Token not valid.");
    }

    //voegt speler toe aan game spelerslijst
    @Override
    public void joinGame(String gameId, String token) throws NoValidTokenException, PlayerNumberexceededException {
        if(validateToken(token)) {
            Speler speler = userTokens.get(token);
            if(speler == null)
                throw new NoValidTokenException("Token not valid.");
            lobby.joinGame(gameId, speler);
        }
    }

    @Override
    public Map<String, Game> getActiveGames(String token) throws NoValidTokenException {
        if(validateToken(token))
            return lobby.getActiveGames();
        return null;
    }

    //////////////////////////////////// Game ///////////////////////////////////////////
    @Override
    public void flipCard(String token, String gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException {
        if(validateToken(token)) {
            Speler speler = userTokens.get(token);
            lobby.getActiveGames().get(gameId).flipCard(x, y, speler);
        }
    }

}
