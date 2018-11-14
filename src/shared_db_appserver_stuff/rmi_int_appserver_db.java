package shared_db_appserver_stuff;

import application_server.memory_spel.Game;
import application_server.memory_spel.Lobby;
import exceptions.UsernameAlreadyInUseException;
import application_server.memory_spel.Speler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Created by ruben on 26/10/18.
 */
public interface rmi_int_appserver_db extends Remote {

    String createUser(String username, String passwdHash) throws UsernameAlreadyInUseException, RemoteException;

    void setUsertoken(Speler speler, String token) throws RemoteException;

    int createGame(String creator, String createdate, boolean started, int aantalspelers, int bordgrootte, int layout, String bordspeltypes, String bordspelfaceup) throws RemoteException;

    void addSpelerToGame(int userid, int gameid) throws RemoteException;

    void removeSpelerToGame(int userid, int gameid) throws RemoteException;

    Map<Integer, Game> getAllGames() throws RemoteException;

    List<Speler> getAllSpelers() throws  RemoteException;

    //return null als speler niet bestaat
    Speler getSpeler(String username) throws RemoteException;

    void changeCredentials(String username, String passwdHash) throws UsernameAlreadyInUseException, RemoteException; //verander usernamen en password, enkel dingen vervangen die niet null zijn

    void deleteGame(int gameId) throws RemoteException;

    void updateFaceUp(int gameid,String data)throws RemoteException;

    //returned null if geen lobby
  //  Lobby getLobby() throws RemoteException;

  //  void persistLobby(Lobby lobby) throws RemoteException;



  //  void addNewBordspel(int layout, int grootte) throws RemoteException;

  //  void addKaart(int xpos, int ypos, int bordspelid) throws RemoteException;

 //   void flipKaart(int kaartid, boolean faceup) throws RemoteException;

    //boolean validateUsertoken(Speler speler) throws RemoteException;

}
