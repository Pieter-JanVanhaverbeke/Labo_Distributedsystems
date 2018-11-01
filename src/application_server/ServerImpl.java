package application_server;

import application_server.Utils.Utils;
import application_server.memory_spel.Game;
import application_server.memory_spel.Lobby;
import application_server.memory_spel.Speler;
import exceptions.*;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.rmi_int_client_appserver;
import shared_db_appserver_stuff.rmi_int_appserver_db;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import static application_server.Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver {
    private Lobby lobby;
    private rmi_int_appserver_db impl;

    public ServerImpl() throws RemoteException {

        Registry registryServer = LocateRegistry.getRegistry("localhost", 13001);
        try {
            impl = (rmi_int_appserver_db) registryServer.lookup("DbServerImplService");
            System.out.println("DB connection ok");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        //haal lobby uit db als al bestaat
        //...

        //voorlopig eigen lobby maken tot db werkt
        lobby = Lobby.getLobby();
    }

    //////////////////////////////// Control //////////////////////////////////////////
    @Override
    public String registrerNewClient(String username, String passwdHash) throws UsernameAlreadyInUseException, RemoteException {
        String token = impl.createUser(username, passwdHash);
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        return token;
    }

    @Override
    public String logIn(String username, String passwordHash) throws WrongPasswordException, UserDoesNotExistException, RemoteException {

        Speler speler = impl.getSpeler(username);

        if (speler == null) {
            throw new UserDoesNotExistException("De gebruiker met gebruikersnaam: " + username + " bestaat niet.");
        }

        if (passwordHash.equals(speler.getPasswordHash())) {
            return Utils.generateUserToken(username);
        } else {
            throw new WrongPasswordException("Het wachtwoord is verkeert.");
        }
    }


    //TODO: DB dingen toevoegen
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
