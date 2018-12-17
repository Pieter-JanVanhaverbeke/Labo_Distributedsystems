package shared_client_appserver_stuff;

import exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface rmi_int_client_appserver extends Remote {

    //////////////////////////////// Control //////////////////////////////////////////
    String registrerNewClient(String username, String passwdHash, String salt, rmi_int_client_appserver_updater clientUpdater) throws UsernameAlreadyInUseException, RemoteException, NotBoundException, NoServerAvailableException;

    void registerClient(String token, rmi_int_client_appserver_updater clientUpdater) throws NoValidTokenException, RemoteException, NoServerAvailableException;

    void unregisterClient(String token) throws NoServerAvailableException, RemoteException, NoValidTokenException;

    String logIn(String username, String passwordHash, rmi_int_client_appserver_updater clientUpdater) throws WrongPasswordException, UserDoesNotExistException, RemoteException, NotBoundException, NoServerAvailableException;

    void logout(String clientId) throws RemoteException;

    String getSalt(String username) throws RemoteException, UserDoesNotExistException, NoServerAvailableException;

    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    String createGame(int aantalSpelers, int bordGrootte, String token, int style) throws RemoteException, GameNotCreatedException, NoValidTokenException, InternalException, NoServerAvailableException;

    void joinGame(String gameId, String token) throws RemoteException, NoValidTokenException, PlayerNumberExceededException, InternalException, NoServerAvailableException;

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    void unJoinGame(String gameId, String token) throws RemoteException, NoValidTokenException, GameAlreadyStartedException, InternalException, NoServerAvailableException;

    List<GameInfo> getActiveGamesList(String token) throws RemoteException, NoValidTokenException, InternalException, NoServerAvailableException;

    GameInfo getGameForLobby(String token, String gameId) throws RemoteException, NoValidTokenException, InternalException, NoServerAvailableException;

    GameInfo getGameForPlaying(String token, String gameId, boolean reallocation) throws RemoteException, NoValidTokenException, InternalException, NoServerAvailableException;

    void registerWatcher(String token, String gameId) throws RemoteException, NoServerAvailableException;

    void unRegisterWatcher(String token, String gameId) throws RemoteException, NoServerAvailableException;

    //////////////////////////////////// Game ///////////////////////////////////////////
    void flipCard(String token, String gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException, InternalException, NoServerAvailableException;

    void startGame(String gameId, String token) throws RemoteException, NoValidTokenException, InternalException, NoServerAvailableException;

    void deleteGame(String gameId) throws RemoteException, NoServerAvailableException;

    void checkUpperGamesCount() throws RemoteException;

    void checkLowerGamesCount() throws RemoteException;

}
