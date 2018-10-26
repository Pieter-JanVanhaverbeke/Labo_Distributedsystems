package rmi_int_client_appserver;

import exceptions.*;
import memory_spel.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface rmi_int_client_appserver extends Remote {

    //////////////////////////////////// Control /////////////////////////////////////////
    String RegistrerNewClient(String username, String passwdHash) throws RemoteException, UsernameAlreadyInUseException;

    String logIn(String username, String passwdHash) throws RemoteException, LoginFailedException;

    void logOut(String token) throws RemoteException;

    //////////////////////////////////// Lobby /////////////////////////////////////////
    String createGame(int aantalSpelers, int bordGrootte, String token) throws RemoteException, GameNotCreatedException, NoValidTokenException;

    void joinGame(String gameId, String token) throws RemoteException, NoValidTokenException, PlayerNumberexceededException;

    Map<String, Game> getActiveGames(String token) throws RemoteException, NoValidTokenException;


    //////////////////////////////////// Game ///////////////////////////////////////////
    void flipCard(String token, String gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException;







}
