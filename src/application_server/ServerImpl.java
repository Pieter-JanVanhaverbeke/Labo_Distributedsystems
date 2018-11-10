package application_server;

import application_server.Utils.Utils;
import application_server.memory_spel.Game;
import application_server.memory_spel.Lobby;
import application_server.memory_spel.Speler;
import exceptions.*;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.GameUpdate;
import shared_client_appserver_stuff.rmi_int_client_appserver;
import shared_db_appserver_stuff.rmi_int_appserver_db;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static application_server.Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver, Serializable {
    public static Lobby lobby;
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
        lobby = impl.getLobby();
        if(lobby == null) {
            lobby = new Lobby();
            impl.persistLobby(lobby);
        }
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

       else if (passwordHash.equals(speler.getPasswordHash())) {
            return Utils.generateUserToken(username);
        } else {
            throw new WrongPasswordException("Het wachtwoord is verkeert.");
        }
    }


    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    @Override
    public String createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException {
        String creator = validateToken(token).getUsername();
        String gameId = lobby.createNewGame(aantalSpelers, bordGrootte, creator);
        //TODO: DB

        //Database invoegen
        try {
            impl.addGame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return gameId;
    }

    //voegt speler toe aan game spelerslijst
    @Override
    public void joinGame(String gameId, String token) throws NoValidTokenException, PlayerNumberExceededException {
        Speler speler = validateToken(token);
        lobby.joinGame(gameId, speler);


        //TODO: id als int zien?
        //Adden database
        int gameid = Integer.parseInt(gameId);

        try {
            impl.addSpelerToGame(speler.getSpelerId(),gameid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    @Override
    public void unJoinGame(String gameId, String token) throws NoValidTokenException, GameAlreadyStartedException {
        Speler speler = validateToken(token);
        lobby.unJoinGame(gameId, speler);



        //TODO: id als int zien?
        //Adden database
        int gameid = Integer.parseInt(gameId);
        try {
            impl.addSpelerToGame(speler.getSpelerId(),gameid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @Override
    public List<GameInfo> getActiveGamesList(String token) throws NoValidTokenException {
        validateToken(token);
        return lobby.getActiveGamesList();
    }

    @Override
    public GameInfo getGame(String token, String gameId) throws NoValidTokenException {
        validateToken(token);
        return new GameInfo(lobby.getGame(gameId));
    }

    //////////////////////////////////// Game ///////////////////////////////////////////
    @Override
    public void flipCard(String token, String gameId, int x, int y) throws NoValidTokenException, NotYourTurnException, NotEnoughSpelersException {
        Speler speler = validateToken(token);
        lobby.getActiveGames().get(gameId).flipCard(x, y, speler);

        //wijziging => stuur respons naar user
        notify();
        //TODO: DB

    }

    @Override
    public void startGame(String gameId, String token) throws NoValidTokenException {
        validateToken(token);
        lobby.getGame(gameId).setStarted(true);
    }

    //TODO: mss met versie nrs werken
    @Override
    public GameUpdate gameUpdate(String gameId, String token) throws NoValidTokenException {
        validateToken(token);
        Game game = lobby.getGame(gameId);

        //wacht op wijziging
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new GameUpdate(game.getSpelerbeurt(), game.getBordspel().getBordRemote());
    }


}
