package application_server;

import DbConnection.dbConnection;
import Utils.Utils;
import exceptions.GameNotCreatedException;
import exceptions.NoValidTokenException;
import exceptions.UsernameAlreadyInUseException;
import memory_spel.Game;
import memory_spel.Lobby;
import memory_spel.Speler;
import rmi_int_client_appserver.rmi_int_client_appserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver {
    ArrayList<String> users = new ArrayList<>(); //wat is dit?
    HashMap<String, String> clientList = new HashMap<>(); // = account die ooit zijn aangemaakt (zit in db => list in memory nodig?)
    private HashMap<String, Speler> userTokens = new HashMap<>(); //bevat de huidig uitgeleende tokens ( = aangemelde users)

    private Lobby lobby;

    public ServerImpl() throws RemoteException {
        lobby = new Lobby();
    }


    @Override
    public String RegistrerNewClient(String username, String password) throws UsernameAlreadyInUseException {
        if(dbConnection.getUserSet().contains(username)){
            System.out.println("gebruikersnaam al gebruikt");
            throw new UsernameAlreadyInUseException();
        }

        //Wachtwoord Hashen en naar databank sturen(bij de client hashen)

        dbConnection.insert(username,password);
        String token = Utils.generateUserToken(username);
        Speler speler = new Speler(username);
        userTokens.put(token, speler);
  //      clientList.put(username,password);           //later opslaan in databank ipv applicatielaag
  //      dbConnection.insert(username,password);
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        return token;

   }

    @Override
    public String logIn(String username, String password) {
        String token = Utils.generateUserToken(username);
        Speler speler = null; // = get speler op username
        userTokens.put(token, speler);
        return token;
    }

    @Override
    public String createGame(int aantalSpelers, int bordGrootte, String token) throws GameNotCreatedException, NoValidTokenException {
        if(validateToken(token))
            return lobby.createNewGame(aantalSpelers, bordGrootte);
        throw new NoValidTokenException("Token not valid.");
    }

    @Override
    public void joinGame(String gameId, String token) throws NoValidTokenException {
        if(validateToken(token)) {
            Speler speler = userTokens.get(token);
            lobby.joinGame(gameId, speler);
        }
        throw new NoValidTokenException("Token not valid.");
    }

    @Override
    public Map<String, Game> getActiveGames(String token) throws NoValidTokenException {
        if(validateToken(token))
            return lobby.getActiveGames();
        throw new NoValidTokenException("Token not valid.");

    }

    @Override
    public void logout(String token) {

    }

    @Override
    public void exitGame(String token) {

    }

    @Override
    public void flipCard(String token, String gameId, int card) throws RemoteException {

    }

}
