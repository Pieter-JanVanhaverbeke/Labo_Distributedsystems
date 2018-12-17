package shared_db_appserver_stuff;

import application_server.memory_spel.Game;
import db_server.DbConnection.Chorde.Node;
import db_server.DbConnection.Chorde.Peer;
import exceptions.UserDoesNotExistException;
import exceptions.UsernameAlreadyInUseException;
import application_server.memory_spel.Speler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ruben on 26/10/18.
 */
public interface rmi_int_appserver_db extends Remote {

    int createUser(String username, String s, String s1) throws UsernameAlreadyInUseException, RemoteException;

    String getSalt(String username) throws RemoteException, UserDoesNotExistException;

    //void setUsertoken(Speler speler, String token) throws RemoteException;

    void addSpelerToGame(int userid, String gameid) throws RemoteException;

    void removeSpelerToGame(int userid, String gameid) throws RemoteException;

    Map<String, Game> getAllGames() throws RemoteException;

    List<Speler> getAllSpelers() throws  RemoteException;

    //return null als speler niet bestaat
    Speler getSpeler(String username) throws RemoteException;

    //void changeCredentials(String username, String passwdHash) throws UsernameAlreadyInUseException, RemoteException; //verander usernamen en password, enkel dingen vervangen die niet null zijn

    void deleteGame(String gameId) throws RemoteException;

    String createGame(String creator, String createdate, boolean started, int aantalspelers, int bordgrootte, int layout, String bordspeltypes, String bordspelfaceup, String ip, int port, int id) throws RemoteException;

    void updateFaceUp(String gameid,String data)throws RemoteException;

    String getFaceUp(String gameid)throws RemoteException;

    Game getGame(String gameId) throws RemoteException;

    List<Integer> getAlleSpelerid (String gameid)throws RemoteException;

    Speler getSpeler(int spelerid) throws RemoteException;

    ArrayList<Integer> getSpelerPunten(String gameid) throws RemoteException;

    void setStarted(boolean b, String gameId) throws RemoteException;

    void updatePunten(String gameid, int userid, int punten) throws RemoteException;

    void updateGlobalScore(int spelerid, int punten) throws RemoteException;

    void updateSpelersbeurt(String gameid, int spelersberut) throws RemoteException;

    //RINGSTRUCTUUR

    void toevoegRing(Node successorid) throws RemoteException;

    void toevoegNode(Node predecessor) throws RemoteException;

    Node getNode() throws RemoteException;

    void setNode(Node node) throws RemoteException;

    void toevoegPeer(Peer peer) throws RemoteException;

    Peer getPeer() throws RemoteException;

    void setPeer(Peer peer) throws RemoteException;

    void setSuccessor(Peer peer) throws RemoteException;

    void setPredecessor(Peer peer) throws RemoteException;

    void floodCreateUser(String username,String passwordHash, String salt, int eindid) throws RemoteException;

    void floodUpdateGlobalScore(int spelerid, int punten, int eindid) throws RemoteException;

    void floodDeleteGame(String gameId, int eindid) throws RemoteException;

    void connectSuccessor(int port) throws RemoteException;

    Map<String, Game> floodGetAlleGames(Map<String, Game> gamesmap, int eindid, int teller) throws RemoteException;

    void floodRemoveSpelerToGame(int userid, String gameid, int eindid) throws RemoteException;

    void floodUpdatePunten(String gameid, int userid, int punten, int eindid) throws RemoteException;

    String floodGetFaceUp(String gameid, String faceup, int eindid, int teller) throws RemoteException;

    void floodUpdateSpelersbeurt(String gameid, int spelersbeurt, int eindid) throws RemoteException;

    void floodSetStarted(boolean b, String gameId, int eindid) throws RemoteException;

    //void floodAddSpelerToGame(int userid, int gameid, int eindid) throws RemoteException;


    void fullUpdate(Game game) throws RemoteException;

    void toevoegSuccesor(int successorid) throws RemoteException;

    void setPeer(int id) throws RemoteException;


    //returned null if geen lobby
  //  Lobby getLobby() throws RemoteException;

  //  void persistLobby(Lobby lobby) throws RemoteException;



  //  void addNewBordspel(int layout, int grootte) throws RemoteException;

  //  void addKaart(int xpos, int ypos, int bordspelid) throws RemoteException;

 //   void flipKaart(int kaartid, boolean faceup) throws RemoteException;

    //boolean validateUsertoken(Speler speler) throws RemoteException;

}
