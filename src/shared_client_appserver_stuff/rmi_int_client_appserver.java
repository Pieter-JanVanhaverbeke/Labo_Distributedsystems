package shared_client_appserver_stuff;

import exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface rmi_int_client_appserver extends Remote {

    //////////////////////////////// Control //////////////////////////////////////////
    String registrerNewClient(String username, byte[] passwdHash, byte[] salt, rmi_int_client_appserver_updater clientUpdater) throws UsernameAlreadyInUseException, RemoteException, NotBoundException;

    String logIn(String username, byte[] passwordHash, rmi_int_client_appserver_updater clientUpdater) throws WrongPasswordException, UserDoesNotExistException, RemoteException, NotBoundException;

    void logout(String clientId) throws RemoteException;

    byte[] getSalt(String username) throws RemoteException,UserDoesNotExistException;

    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    int createGame(int aantalSpelers, int bordGrootte, String token, int style) throws RemoteException, GameNotCreatedException, NoValidTokenException, InternalException;

    void joinGame(int gameId, String token) throws RemoteException, NoValidTokenException, PlayerNumberExceededException, InternalException;

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    void unJoinGame(int gameId, String token) throws RemoteException, NoValidTokenException, GameAlreadyStartedException, InternalException;

    List<GameInfo> getActiveGamesList(String token) throws RemoteException, NoValidTokenException, InternalException;

    GameInfo getGame(String token, int gameId) throws RemoteException, NoValidTokenException, InternalException;

    void registerWatcher(String token, int gameId) throws RemoteException;

    void unRegisterWatcher(String token, int gameId) throws RemoteException;

    //////////////////////////////////// Game ///////////////////////////////////////////
    void flipCard(String token, int gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException, InternalException;

    void startGame(int gameId, String token) throws RemoteException, NoValidTokenException, InternalException;

    void deleteGame(int gameId) throws RemoteException;
}
