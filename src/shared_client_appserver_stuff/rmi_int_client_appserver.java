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
    int createGame(int aantalSpelers, int bordGrootte, String token, int style) throws RemoteException, GameNotCreatedException, NoValidTokenException, InternalException;

    void joinGame(int gameId, String token) throws RemoteException, NoValidTokenException, PlayerNumberExceededException, InternalException;

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    void unJoinGame(int gameId, String token) throws RemoteException, NoValidTokenException, GameAlreadyStartedException, InternalException;

    List<GameInfo> getActiveGamesList(String token) throws RemoteException, NoValidTokenException, InternalException;

    GameInfo getGame(String token, int gameId) throws RemoteException, NoValidTokenException, InternalException;

    //////////////////////////////////// Game ///////////////////////////////////////////
    void flipCard(String token, int gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException, InternalException;

    void startGame(int gameId, String token) throws RemoteException, NoValidTokenException, InternalException;

    GameUpdate gameUpdate(int gameId, String token) throws RemoteException, NoValidTokenException, InternalException;
}
