package rmi_int_client_appserver;

import exceptions.GameNotCreatedException;
import exceptions.LoginFailedException;
import exceptions.NoValidTokenException;
import exceptions.UsernameAlreadyInUseException;
import memory_spel.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface rmi_int_client_appserver extends Remote {

    //////////////////////////////////// Control /////////////////////////////////////////
    String RegistrerNewClient(String username, String passwdHash) throws RemoteException, UsernameAlreadyInUseException;

    String logIn(String username, String passwdHash) throws RemoteException, LoginFailedException;

    String createGame(int aantalSpelers, int bordGrootte, String token) throws RemoteException, GameNotCreatedException, NoValidTokenException;

    void joinGame(String gameId, String token) throws RemoteException, NoValidTokenException;

    Map<String, Game> getActiveGames(String token) throws RemoteException, NoValidTokenException;

    void logout(String token) throws RemoteException;

    void exitGame(String token) throws RemoteException;

    void flipCard(String token, String gameId, int card) throws RemoteException;


    //////////////////////////////////// Game ///////////////////////////////////////////







}
