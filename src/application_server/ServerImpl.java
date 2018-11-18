package application_server;

import application_server.memory_spel.Lobby;
import application_server.memory_spel.Speler;
import exceptions.*;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.rmi_int_client_appserver;
import shared_client_appserver_stuff.rmi_int_client_appserver_updater;
import shared_db_appserver_stuff.rmi_int_appserver_db;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static application_server.ServerMain.clients;
import static application_server.Utils.Utils.generateUserToken;
import static application_server.Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver, Serializable {
    public static rmi_int_appserver_db impl;

    public static final String ADDRESSDB = "localhost";
    public static final int PORTDB = 13001;


    public ServerImpl() throws RemoteException {

        Registry registryServer = LocateRegistry.getRegistry(ADDRESSDB, PORTDB);
        try {
            impl = (rmi_int_appserver_db) registryServer.lookup("DbServerImplService");
            System.out.println("DB connection ok");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////// Control //////////////////////////////////////////
    @Override
    public String registrerNewClient(String username, String passwdHash, rmi_int_client_appserver_updater clientUpdater) throws UsernameAlreadyInUseException, RemoteException, NotBoundException {
        int clientId = impl.createUser(username, passwdHash);
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        clients.put(username, clientUpdater);
        return generateUserToken(username);
    }

    @Override
    public String logIn(String username, String passwordHash, rmi_int_client_appserver_updater clientUpdater) throws WrongPasswordException, UserDoesNotExistException, RemoteException, NotBoundException {
        Speler speler = impl.getSpeler(username);

        if (speler == null) {
            throw new UserDoesNotExistException("De gebruiker met gebruikersnaam: " + username + " bestaat niet.");
        }

       else if (passwordHash.equals(speler.getPasswordHash())) {
            //client toevoegen voor updates naar te sturen
            clients.put(username, clientUpdater);
            return generateUserToken(username);
        } else {
            throw new WrongPasswordException("Het wachtwoord is verkeert.");
        }
    }

    @Override
    public void logout(String clientUsername) {
        clients.remove(clientUsername);
    }


    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    @Override
    public int createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException, InternalException {
        try {
            String creator = validateToken(token).getUsername();
            int gameId = Lobby.createNewGame(aantalSpelers, bordGrootte, creator, style);
            updateClients();
            return gameId;
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //voegt speler toe aan game spelerslijst
    @Override
    public void joinGame(int gameId, String token) throws NoValidTokenException, PlayerNumberExceededException, InternalException {
        try {
            Speler speler = validateToken(token);
            Lobby.joinGame(gameId, speler);
            updateClients();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    @Override
    public void unJoinGame(int gameId, String token) throws NoValidTokenException, GameAlreadyStartedException, InternalException {
        try {
            Speler speler = validateToken(token);
            Lobby.unJoinGame(gameId, speler);
            updateClients();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");

        }
    }

    @Override
    public List<GameInfo> getActiveGamesList(String token) throws NoValidTokenException, InternalException {
        try {
            validateToken(token);
            return Lobby.getActiveGamesList();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");

        }
    }

    @Override
    public GameInfo getGame(String token, int gameId) throws NoValidTokenException, InternalException {
        try {
            validateToken(token);
            return new GameInfo(Lobby.getGame(gameId));
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //////////////////////////////////// Game ///////////////////////////////////////////
    @Override
    public void flipCard(String token, int gameId, int x, int y) throws NoValidTokenException, NotYourTurnException, NotEnoughSpelersException, InternalException {
        try {
            Speler speler = validateToken(token);
            Lobby.flipCard(gameId, x, y, speler);

        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    @Override
    public void startGame(int gameId, String token) throws NoValidTokenException, InternalException {
        try {
            validateToken(token);
            Lobby.getGame(gameId).setStarted(true);
            impl.setStarted(true, gameId);
            updateClients();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }


    @Override
    public void deleteGame(int gameId) throws RemoteException{
        Lobby.deleteGame(gameId);
        updateClients();
    }

    private void updateClients() throws RemoteException {
        for(rmi_int_client_appserver_updater updater: clients.values()){
            updater.updateLobby(Lobby.getActiveGamesList());
        }
    }


}
