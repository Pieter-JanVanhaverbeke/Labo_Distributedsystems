package application_server;

import application_server.DbConnection.dbConnection;
import application_server.Utils.Utils;
import shared_client_appserver_stuff.GameInfo;
import exceptions.*;
import application_server.memory_spel.Game;
import application_server.memory_spel.Lobby;
import application_server.memory_spel.Speler;
import shared_client_appserver_stuff.rmi_int_client_appserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static application_server.Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver {
    private Lobby lobby;

    public ServerImpl() throws RemoteException {
        // setup db connection
        dbConnection.connect();

        //haal lobby uit db
        //...

        //voorlopig eigen lobby maken tot db werkt
        lobby = Lobby.getLobby();
    }

    //////////////////////////////// Control //////////////////////////////////////////
    @Override
    public String registrerNewClient(String username, String passwdHash) throws UsernameAlreadyInUseException {
        if(dbConnection.getUserSet().contains(username)){
            System.out.println("gebruikersnaam al gebruikt");
            throw new UsernameAlreadyInUseException(username);
        }

        String token = Utils.generateUserToken(username);
        Speler speler = new Speler(username);
        //dbConnection.insert(username,passwdHash); //TODO: db
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        return token;

   }

    @Override
    public String logIn(String username, String password) {
        //TODO: check db voor credentials
        Speler speler = null; //TODO: = get speler op username uit db
        String token = Utils.generateUserToken(username);
        return token;
    }


    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    @Override
    public String createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException {
        String creator = validateToken(token).getUsername();
        return lobby.createNewGame(aantalSpelers, bordGrootte, creator);
    }

    //voegt speler toe aan game spelerslijst
    @Override
    public void joinGame(String gameId, String token) throws NoValidTokenException, PlayerNumberExceededException {
        Speler speler = validateToken(token);
        lobby.joinGame(gameId, speler);
    }

    //niet gebruiken!
    @Override
    public Map<String, Game> getActiveGames(String token) throws NoValidTokenException {
        validateToken(token);
        return lobby.getActiveGames();
    }

    @Override
    public List<GameInfo> getActiveGamesList(String token) throws NoValidTokenException {
        validateToken(token);
        return lobby.getActiveGamesList();
    }

    //////////////////////////////////// Game ///////////////////////////////////////////
    @Override
    public void flipCard(String token, String gameId, int x, int y) throws NoValidTokenException, NotYourTurnException, NotEnoughSpelersException {
        Speler speler = validateToken(token);
        lobby.getActiveGames().get(gameId).flipCard(x, y, speler);
    }

    @Override
    public int[][] getBord(String token, String gameId) throws NoValidTokenException {
        validateToken(token);
        return lobby.getGame(gameId).getBordspel().getBordRemote();
    }


}
