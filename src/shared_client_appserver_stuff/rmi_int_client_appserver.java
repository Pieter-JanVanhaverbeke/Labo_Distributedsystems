package shared_client_appserver_stuff;

import application_server.memory_spel.Game;
import exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface rmi_int_client_appserver extends Remote {

    //////////////////////////////////// Control /////////////////////////////////////////
    String registrerNewClient(String username, String passwdHash) throws RemoteException, UsernameAlreadyInUseException;

    String logIn(String username, String passwdHash) throws RemoteException, LoginFailedException;

    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    String createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException;

    void joinGame(String gameId, String token) throws RemoteException, NoValidTokenException, PlayerNumberExceededException;

    Map<String, Game> getActiveGames(String token) throws RemoteException, NoValidTokenException;

    List<GameInfo> getActiveGamesList(String token) throws RemoteException, NoValidTokenException;


    //////////////////////////////////// Game ///////////////////////////////////////////
    void flipCard(String token, String gameId, int x, int y) throws RemoteException, NoValidTokenException, NotYourTurnException, NotEnoughSpelersException;

    int[][] getBord(String token, String gameId) throws RemoteException, NoValidTokenException;





}
