package shared_client_appserver_stuff;

import exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface rmi_int_client_appserver extends Remote {

    //////////////////////////////////// Control /////////////////////////////////////////
    String registrerNewClient(String username, String passwdHash) throws RemoteException, UsernameAlreadyInUseException;

    String logIn(String username, String passwdHash) throws RemoteException, LoginFailedException, WrongPasswordException, UserDoesNotExistException;

    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    String createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException;

    void joinGame(String gameId, String token) throws RemoteException, NoValidTokenException, PlayerNumberExceededException;

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    void unJoinGame(String gameId, String token) throws NoValidTokenException, GameAlreadyStartedException;

    List<GameInfo> getActiveGamesList(String token) throws RemoteException, NoValidTokenException;

    GameInfo getGame(String token, String gameId) throws NoValidTokenException;

    //////////////////////////////////// Game ///////////////////////////////////////////
    void flipCard(String token, String gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException;

    int[][] getBord(String token, String gameId) throws RemoteException, NoValidTokenException;

    void startGame(String gameId, String token) throws NoValidTokenException;
}
