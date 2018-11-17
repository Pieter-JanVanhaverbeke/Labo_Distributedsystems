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

import static application_server.Utils.Utils.generateUserToken;
import static application_server.Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver, Serializable {
    public static rmi_int_appserver_db impl;

    public ServerImpl() throws RemoteException {

        Registry registryServer = LocateRegistry.getRegistry("localhost", 13001);
        try {
            impl = (rmi_int_appserver_db) registryServer.lookup("DbServerImplService");
            System.out.println("DB connection ok");
        } catch (NotBoundException e) {
            e.printStackTrace();
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
            return generateUserToken(username);
        } else {
            throw new WrongPasswordException("Het wachtwoord is verkeert.");
        }
    }


    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    @Override
    public int createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException, InternalException {
        try {
            String creator = validateToken(token).getUsername();
            int gameId = Lobby.createNewGame(aantalSpelers, bordGrootte, creator, style);
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
            Lobby.getActiveGames().get(gameId).flipCard(x, y, speler);

            //wijziging => stuur respons naar user
            notify();


            //DATABANK

            String faceup = impl.getFaceUp(gameId);

            //LOGICA OM UP TE DATEN NAAR DATABENK  TODO mss andere plaats zetten

            int bordlengte = (int) Math.sqrt(faceup.length()/2);                //bordspelsize halen uit lengte string.
            int coordinaat = 2*bordlengte*x+2*y;           //coordinaat dat moet vervangen worden in string.
            char face = '0';



            StringBuilder sb = new StringBuilder(faceup);


            if(faceup.charAt(coordinaat)==face){
                 sb.setCharAt(coordinaat, '1');
            }
            else{
                sb.setCharAt(coordinaat,'0');
            }

            faceup=sb.toString();

            impl.updateFaceUp(gameId,faceup);

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //TODO: mss met versie nrs werken
    @Override
    public GameUpdate gameUpdate(int gameId, String token) throws NoValidTokenException, InternalException {
        try {
            validateToken(token);
            Game game = Lobby.getGame(gameId);
            //wacht op wijziging van server
            wait();
            return new GameUpdate(game.getSpelerbeurt(), game.getBordspel().getBordRemote());
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new InternalException("update mechanisme is interrupted.");
        }
    }


}
