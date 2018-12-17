package db_server.DbConnection;

import db_server.DbConnection.Chorde.Node;
import db_server.DbConnection.Chorde.Peer;
import application_server.memory_spel.*;
import exceptions.UserDoesNotExistException;
import shared_db_appserver_stuff.rmi_int_appserver_db;
import exceptions.UsernameAlreadyInUseException;
import org.joda.time.DateTime;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

import static db_server.DbConnection.dbConnection.connect;
import static db_server.DbMain.databankstring;

/**
 * Bevat alle methodes die de applicatieserver op de databankserver kan oproepen.
 */
public class dbImpl extends UnicastRemoteObject implements rmi_int_appserver_db, Serializable {
    private HashMap<String, Speler> userTokens;//bevat de huidig uitgeleende tokens ( = aangemelde users)
    private Node node;
    private Peer peer;
    private int successorid;
    private int id;
    private rmi_int_appserver_db implDBvolgende;


    public dbImpl() throws RemoteException {
        userTokens = new HashMap<>();
     //   node = new Node();
        peer = new Peer();
        successorid = -1;
        id = -1;




        /*
        node = new Node();
        node.toevoeg(null);             //TODO AANPASSEN
        int id = 0; //TODO INPUT ID DATABANKEN
        node.setId(id);
        */
    }



    /**
     *Methode die user aanmaakt, meegegeven argumenten zijn username, passwordHash en de salt van het paswoord.
     * Return is een int, primary key van de user dat de huidige tijd bedraagd
     * @param username gebruikersnaam van de gemaakte user
     * @param passwordHash de meegegeven password die gehashed en gesalted is.
     * @param salt de meegegeven salt
     * @return return van de primary key
     *
     * */
    @Override
    public synchronized int createUser(String username, String passwordHash, String salt) throws UsernameAlreadyInUseException {
        int id = -1;

        if (dbConnection.getUserSet(databankstring).contains(username)) {
            System.out.println("gebruikersnaam: " + username + " al gebruikt");
            throw new UsernameAlreadyInUseException(username);
        } else {
            String sql = "INSERT INTO Users(username,password,globalScore,salt) VALUES(?,?,?,?)";
            Connection conn = connect(databankstring);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, passwordHash);
                pstmt.setInt(3, 0);
                pstmt.setString(4, salt);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try (
                    PreparedStatement psmt = conn.prepareStatement("SELECT last_insert_rowid() AS NewID;")) {
                ResultSet resultSet2 = psmt.executeQuery();
                while (resultSet2.next()) {
                    id = resultSet2.getInt("NewID");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            return id;
        }
    }
    /** Methode dat de create user rondbrengt naar alle databankservers
     *   @param username gebruikersnaam van de gemaakte user
     *   @param passwordHash de meegegeven password die gehashed en gesalted is.
     *   @param salt de meegegeven salt
     *   @param eindid de id van de  eerste van de kring van databases
     *
     * */
    @Override
    public synchronized void floodCreateUser(String username,String passwordHash, String salt, int eindid){
        try {
            createUser( username,  passwordHash,  salt);
        } catch (UsernameAlreadyInUseException e) {
            e.printStackTrace();
        }


        if(eindid!=peer.getId()){              //TODO goed eine setten
            try {
                implDBvolgende.floodCreateUser(username, passwordHash,  salt,  eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    /** Methode om de globale score aan te passen van een speler in een databank
     * @param spelerid spelerid waar je punten wilt updaten
     * @param punten waarde van punten dat speler nu heeft
     *
     * */
    public synchronized void updateGlobalScore(int spelerid, int punten){
        String sql = "UPDATE Users SET globalScore = ? WHERE spelerid = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, punten);
            pstmt.setInt(2, spelerid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /** Methode om de methode updateGlobalScore toe te passen rond elke databank
     * @param spelerid spelerid waar je punten wilt updaten
     * @param punten waarde van punten dat speler nu heeft
     * @param eindid de id van de  eerste van de kring van databases
     * */
    @Override
    public synchronized void floodUpdateGlobalScore(int spelerid, int punten, int eindid){

        updateGlobalScore( spelerid,  punten);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodUpdateGlobalScore(spelerid, punten, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /** Methode die als ingegeven argument de gebruiker de salt teruggeeft van de gebruiker waarvoor gehashed werd
     * @param username gebruikersnaam van de opgevraagde salt
     * @return salt de salt van de gebruikersnaam
     *
     * */
    @Override
    public synchronized String getSalt(String username) throws UserDoesNotExistException {
        String salt = "";
        String sql = "SELECT * FROM Users WHERE username = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                salt = rs.getString("salt");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //if user niet bestaat => throw UserDoesNotExistException
        if (salt.equals("")) {
            throw new UserDoesNotExistException("user does not exist");
        }

        return salt;
    }

    /*@Override
    public void setUsertoken(Speler speler, String token) {
        String username = speler.getUsername();
        String sql = "UPDATE Users SET token = ? , WHERE username = ?";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        updateTime(username);           //setten van tijd usertoken
    }
*/
    /** Methode om een game te creëren in een databank, return type is int en geeft de primary key terug
     * @param creator   de naam van de creator van het spel
     * @param createdate de datum van het gemaakte spel
     * @param started boolean dat zegt of een spel al begonnen is
     * @param aantalspelers int die aantal spelers meegeeft
     * @param bordgrootte int van 1-3, waarbij de bordgrootte gedefinieerd is 1=4X4 2=6X6 3=8X8
     * @param layout int die layout type meegeeft
     * @param bordspeltypes String van een matrix van de type kaartjes, de rijen worden achtereenvolgens achter elkaar geplaatst tot op 1 rij
     * @param bordspelfaceup String van een matrix van de faceup kaartjes, de rijen worden achtereenvolgens achter elkaar geplaatst tot op 1 rij
     * @param serverIp String van het ip van de server
     * @param serverId meegegeven id van de applicatieserver
     * @param serverPort meegegeven applicatiepoort
     * */
    @Override
    public synchronized String createGame(String creator, String createdate, boolean started, int aantalspelers, int bordgrootte, int layout, String bordspeltypes, String bordspelfaceup, String serverIp, int serverPort, int serverId) {

        String id = String.valueOf(System.currentTimeMillis());
        String sql = "INSERT INTO Game(creator,createdate,started,aantalspelers,bordgrootte,layout,bordspeltypes,bordspelfaceup,spelersbeurt, serverip, serverport, serverid, gameid)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Connection conn = connect(databankstring);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, creator);
            pstmt.setString(2, createdate);
            pstmt.setBoolean(3, started);
            pstmt.setInt(4, aantalspelers);
            pstmt.setInt(5, bordgrootte);
            pstmt.setInt(6, layout);
            pstmt.setString(7, bordspeltypes);
            pstmt.setString(8, bordspelfaceup);
            pstmt.setInt(9, 0);
            pstmt.setString(10, serverIp);
            pstmt.setInt(11, serverPort);
            pstmt.setInt(12, serverId);
            //System.out.println(System.currentTimeMillis());
            pstmt.setString(13, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*try (PreparedStatement psmt = conn.prepareStatement("SELECT last_insert_rowid() AS NewID;")) {
            ResultSet resultSet2 = psmt.executeQuery();
            while (resultSet2.next()) {
                id = resultSet2.getString("NewID");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }*/

        return id;

    }

    /** Methode om een spel te verwijderen uit een databank met key gameId
     * @param gameId String van de id van de game dat gedelete moet worden
     *
     * */
    @Override
    public synchronized void deleteGame(String gameId) {
        String sql = "DELETE FROM Game WHERE gameid=?";
        Connection conn = connect(databankstring);
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql2 = "DELETE FROM GameSpelertable WHERE gameid=?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setString(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }
    /** Methode die rondvraagd aan alle databanken om game te deleten
     * @param gameId String van de id van de game dat gedelete moet worden
     * @param eindid de id van de  eerste van de kring van databases
     * */
    @Override
    public synchronized void floodDeleteGame(String gameId, int eindid){//TODO NOG TESTEN
        deleteGame(gameId);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodDeleteGame(gameId, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /** Methode de faceup logica aan te passen aan een game
     * @param gameid String van de primary key van game
     * @param data String van data faceup kaartjes
     *
     *
     * */
        @Override
    public synchronized void updateFaceUp(String gameid,String data){
        String sql = "UPDATE  Game SET bordspelfaceup = ? WHERE gameid=?";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,data);
            pstmt.setString(2, gameid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param userid int die userid meegeeft die moet geadd worden aan server
     * @param gameid String die id is van game waarbij user moet joinen
     */

    @Override
    public synchronized void addSpelerToGame(int userid, String gameid){
        String sql = "INSERT INTO GameSpelertable(userid,gameid,spelerpunten) VALUES(?,?,?)";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userid);
            pstmt.setString(2, gameid);
            pstmt.setInt(3,0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /*void floodAddSpelerToGame(int userid, int gameid, int eindid){

    }
*/
    /** Methode om een speler met userid te verwijderen van een game met bepaalde gameid
     * @param userid int die userid meegeeft die moet verwijderd worden aan server
     * @param gameid String die id is van game waarbij user moet verdwijnen
     *
     * */
    @Override
    public synchronized void removeSpelerToGame(int userid, String gameid){
        String sql = "DELETE FROM GameSpelertable WHERE userid = ? AND gameid = ?";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userid);
            pstmt.setString(2, gameid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**Methode om een removeSpelerToGame toe te passen rond alle databanken
     * @param userid  int die userid meegeeft die moet verwijderd worden aan server
     * @param gameid  String die id is van game waarbij user moet verdwijnen
     * @param eindid  de id van de  eerste van de kring van databases
     *
     * */
    @Override
    public synchronized void floodRemoveSpelerToGame(int userid, String gameid, int eindid){
        removeSpelerToGame(userid, gameid);                                     //TODO NIET OVER ALLE DB GAAN
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodRemoveSpelerToGame(userid, gameid, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /** Methode om de punten te updaten van een spel in de databank
     * @param gameid String met gameid
     * @param userid int die userid meegeeft
     * @param punten int die de nieuwe punten geeft aan user
     * */
    public synchronized void updatePunten(String gameid, int userid, int punten){
        String sql = "UPDATE GameSpelertable SET spelerpunten = ? WHERE gameid = ? AND userid = ?";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, punten);
            pstmt.setString(2, gameid);
            pstmt.setInt(3,userid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Methode die updatePunten doorgeeft naar alle databanken
     * @param gameid String met gameid
     * @param userid int die userid meegeeft
     * @param punten int die de nieuwe punten geeft aan user
     * @param eindid de id van de  eerste van de kring van databases
     * */
    public synchronized void floodUpdatePunten(String gameid, int userid, int punten, int eindid){      //TODO NIET ALLES CHECKEN?
        updatePunten( gameid,  userid, punten);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodUpdatePunten(gameid, userid, punten, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    /** Methode die een spelersbeurt update van game met gameid
     * @param gameid String met gameid
     * @param spelersbeurt int met de spelerid die aan de beurt is (id van het spel)
     * */
    public synchronized void updateSpelersbeurt(String gameid, int spelersbeurt){
        String sql = "UPDATE Game SET spelersbeurt = ? WHERE gameid = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("gameid van spel: " + gameid);
            System.out.println("spelerbeurt: " + spelersbeurt);
            pstmt.setInt(1, spelersbeurt);
            pstmt.setString(2, gameid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Methode die updateSpelersbeurt doorgeeft aan verschillende databanken
     * @param gameid String met gameid
     * @param spelersbeurt int met de spelerid die aan de beurt is (id van het spel)
     * @param eindid de id van de  eerste van de kring van databases
     * */
    public synchronized void floodUpdateSpelersbeurt(String gameid, int spelersbeurt, int eindid){
        updateSpelersbeurt( gameid, spelersbeurt);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodUpdateSpelersbeurt(gameid, spelersbeurt, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param game methode dat een full update doet van een game
     */
    //zet game in db, game bestaat al maar dit is een full update voordat een server shutdownt

    /**
     * Doe een volledige update van de game naar de databank.
     * @param game
     */
    @Override
    public synchronized void fullUpdate(Game game) {

        String sql = "UPDATE  Game SET creator = ?, createdate = ?, started = ?, aantalspelers = ?, bordspelfaceup = ?, spelersbeurt = ?, serverip = ?, serverport = ?, serverid = ? WHERE gameid=?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(10,game.getGameId());
            pstmt.setString(1, game.getCreator());
            pstmt.setString(2, game.getCreateDate());
            pstmt.setBoolean(3, game.isStarted());
            pstmt.setInt(4, game.getAantalspelers());
            pstmt.setString(5, game.getFaceUp());
            pstmt.setInt(6, game.getSpelerbeurt());
            pstmt.setString(7, game.getServerInfo().getIpAddress());
            pstmt.setInt(8, game.getServerInfo().getPortNumber());
            pstmt.setInt(9, game.getServerInfo().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "DELETE FROM GameSpelertable WHERE gameid = ?";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,game.getGameId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Speler speler: game.getSpelers()){
            addSpelerToGame(speler.getSpelerId(), game.getGameId());
        }
    }


    //////////////////////////////////// HaalDatabase ///////////////////////////////////////////

    /** Methode die alle games opvraagd van een databank, return type is map met primary key gameid en value de games
     * @return map met de gameids als key en de games als value
     * */
    @Override //return lege lijst als geen games
    public synchronized Map<String, Game> getAllGames() { //return alle games in db met gameId = key
        Map<String, Game> map = new HashMap<>();
        try {
            Connection conn = connect(databankstring);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Game";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {                         //voor alle games

                String gameid = rs.getString("gameid");
                String creator = rs.getString("creator");
                String createdate = rs.getString("createdate");
                boolean started = rs.getBoolean("started");
                int bordgrootte = rs.getInt("bordgrootte");
                int aantalspelers = rs.getInt("aantalspelers");
                String bordspeltypes = rs.getString("bordspeltypes");
                String bordspelfacup = rs.getString("bordspelfaceup");
                int layout = rs.getInt("layout");
                int spelersbeurt = rs.getInt("spelersbeurt");
                String serverAddress = rs.getString("serverip");
                int serverPort = rs.getInt("serverport");
                int serverId = rs.getInt("serverid");


                String[] valuestypes = bordspeltypes.split("\\s+");
                String[] valuefacup = bordspelfacup.split("\\s+");


                int size = 2 * bordgrootte + 2;
                Bordspel bordspel = new Bordspel(size, size);          //size meegeven
                Kaart bordspelkaarten[][] = new Kaart[size][size];

                //alle gegevens naar kaarten brengen
                for (int i = 0; i < valuestypes.length; i++) {

                    Kaart kaart = new Kaart();
                    int soort = Integer.parseInt(valuestypes[i]);
                    boolean faceup = valuefacup[i].equals("1");
                    kaart.setSoort(soort);
                    kaart.setFaceUp(faceup);
                    bordspelkaarten[i / size][i % size] = kaart;                  //naar matrix omzetten
                }

                //alle gegevens naar bordspel steken
                bordspel.setBord(bordspelkaarten);
                bordspel.setType(layout);

                //alles in game steken
                Game game = new Game(bordgrootte, aantalspelers, creator, layout, gameid);
                game.setServerInfo(new ServerInfo(serverAddress, serverPort, serverId));
                game.setCreateDate(createdate);
                game.setStarted(started);
                game.setBordspel(bordspel);
                game.setSpelerbeurt(spelersbeurt);
                List<Integer> spelerscores = getSpelerPunten(gameid);
                List<Integer> spelerids = getAlleSpelerid(gameid);
                for (int i = 0; i < spelerids.size(); i++) {
                    Speler speler = getSpeler(spelerids.get(i)); //get speler met id
                    game.getSpelers().add(speler);

                    int score = spelerscores.get(i);
                    game.getPuntenlijst().put(speler.getSpelerId(), score);
                }

                map.put(game.getGameId(), game);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    /** Methode die GetAlleGames rondvraagt aan alle databanken, return type is map met primary key gameid en value de games
     * @param gamesmap lege map die zal aangevuld worden voor recursie
     * @param eindid einde id database van kring
     * @param teller teller voor recursie, start default op 0
     * @return map met de gameids als key en de games als value
     * */
    @Override
    public synchronized Map<String, Game> floodGetAlleGames(Map<String, Game> gamesmap, int eindid, int teller) {   //TODO OPLOSSEN GAMES TERUGGEVEN

        if (eindid == this.peer.getId()) {
            teller++;
        }
        if(teller<2) {
            gamesmap.putAll(getAllGames());
            try {
               gamesmap = implDBvolgende.floodGetAlleGames(gamesmap,eindid,teller);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        System.out.println(gamesmap.size());
        return gamesmap;
    }

    /**
     *
     * @param gameId String van gameId
     * @return Game van gameID
     */
    @Override
    public synchronized Game getGame(String gameId) {
        Game game = null;

        String sql = "SELECT * FROM Game WHERE gameid = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gameId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String gameid = rs.getString("gameid");
                String creator = rs.getString("creator");
                String createdate = rs.getString("createdate");
                boolean started = rs.getBoolean("started");
                int bordgrootte = rs.getInt("bordgrootte");
                int aantalspelers = rs.getInt("aantalspelers");
                String bordspeltypes = rs.getString("bordspeltypes");
                String bordspelfacup = rs.getString("bordspelfaceup");
                int layout = rs.getInt("layout");
                int spelersbeurt = rs.getInt("spelersbeurt");
                String serverAddress = rs.getString("serverip");
                int serverPort = rs.getInt("serverport");
                int serverId = rs.getInt("serverid");

                String[] valuestypes = bordspeltypes.split("\\s+");
                String[] valuefacup = bordspelfacup.split("\\s+");


                int size = 2 * bordgrootte + 2;
                Bordspel bordspel = new Bordspel(size, size);          //size meegeven
                Kaart bordspelkaarten[][] = new Kaart[size][size];

                //alle gegevens naar kaarten brengen
                for (int i = 0; i < valuestypes.length; i++) {

                    Kaart kaart = new Kaart();
                    int soort = Integer.parseInt(valuestypes[i]);
                    boolean faceup = valuefacup[i].equals("1");
                    kaart.setSoort(soort);
                    kaart.setFaceUp(faceup);
                    bordspelkaarten[i / size][i % size] = kaart;                  //naar matrix omzetten
                }


                //alle gegevens naar bordspel steken
                bordspel.setBord(bordspelkaarten);
                bordspel.setType(layout);

                //alles in game steken
                game = new Game(bordgrootte, aantalspelers, creator, layout, gameid);
                game.setServerInfo(new ServerInfo(serverAddress, serverPort, serverId));
                game.setCreateDate(createdate);
                game.setStarted(started);
                game.setBordspel(bordspel);
                game.setSpelerbeurt(spelersbeurt);
                List<Integer> spelerscores = getSpelerPunten(gameid);
                List<Integer> spelerids = getAlleSpelerid(gameid);
                for (int i = 0; i < spelerids.size(); i++) {
                    Speler speler = getSpeler(spelerids.get(i)); //get speler met id
                    game.getSpelers().add(speler);

                    int score = spelerscores.get(i);
                    game.getPuntenlijst().put(speler.getSpelerId(), score);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return game;
    }



    /** Methode die info van omgedraaide kaartjes teruggeeft van game met gameid, return type is String die bestaat uit 0 en 1'en
     * @param gameid String van id die je meegeeft
     *
     *
     * */
    @Override
    public synchronized String getFaceUp(String gameid){
            String faceup = "";
            String sql = "SELECT * FROM Game WHERE gameid = ?;";
            try (Connection conn = connect(databankstring);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, gameid);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    faceup = rs.getString("bordspelfaceup");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        return faceup;
    }
    /** Methode die getFaceUp rondvraagt aan alle databanken,  return type is String die bestaat uit 0 en 1'en
     * @param gameid String van id die je meegeeft
     * @param faceup String van kaartjes die faceup zijn
     * @param eindid int van einde databank van kring
     * @param teller int voor recursie, defaultwaarde is 0
     * */
    public synchronized String floodGetFaceUp(String gameid, String faceup, int eindid, int teller){
        if (eindid == this.peer.getId()) {
            teller++;
        }

        if(teller<2) {
            faceup = faceup + getFaceUp(gameid);
            try {
                faceup = implDBvolgende.floodGetFaceUp(gameid,faceup,eindid,teller);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return faceup;
    }

    /** Methode die alle spelers opvraagd aan databank, return type is lijst van de spelers
     * @return lijst van alle spelers opgeslagen in databank
     *
     * */
    @Override
    public synchronized List<Speler> getAllSpelers() {

        List<Speler> spelerslijst = new ArrayList<Speler>();

        try {
            Connection conn = connect(databankstring);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Users";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                int spelerId = rs.getInt("spelerId");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int globalScore = rs.getInt("globalScore");

                Speler speler = new Speler(username);
                speler.setSpelerId(spelerId);
                speler.setGlobalScore(globalScore);
                speler.setPasswordHash(password);
                spelerslijst.add(speler);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return spelerslijst;
    }


    /** Methode die alle spelersids opvraagd van een bepaalde game met gameid, returntype is lijst van spelerids
     * @param gameid String van gameid
     * @return returned lijst van alle integers van spelerids
     * */
    @Override
    public synchronized List<Integer> getAlleSpelerid (String gameid) {
        ArrayList<Integer> speleridlijst = new ArrayList<Integer>();


        String sql = "SELECT userid FROM GameSpelertable WHERE gameid = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gameid);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int spelerid = rs.getInt("userid");
                speleridlijst.add(spelerid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return speleridlijst;

    }
    //  Map<Integer, Speler> getAlleSpelersMetGame(int gameid){


    // }


    /** Methode die een speler teruggeeft met String username
     * @param username String van username
     * @return Speler die gelijk is aan username
     * */
    @Override
    public synchronized Speler getSpeler(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int spelerid = rs.getInt("spelerid");
                String passwordHash = rs.getString("password");
                int globalScore = rs.getInt("globalScore");


                Speler speler = new Speler(username);
                speler.setSpelerId(spelerid);
                speler.setGlobalScore(globalScore);
                speler.setPasswordHash(passwordHash);

                System.out.println("gelukt");

                return speler;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /** Methode die speler ophaald met een spelerid
     * @param spelerid int van gevraagde spelerid
     * @return Speler met waarde spelerid
     * */
    @Override
    public synchronized Speler getSpeler(int spelerid) {
        String sql = "SELECT * FROM Users WHERE spelerid = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, spelerid);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String passwordHash = rs.getString("password");
                int globalScore = rs.getInt("globalScore");


                Speler speler = new Speler(username);
                speler.setSpelerId(spelerid);
                speler.setGlobalScore(globalScore);
                speler.setPasswordHash(passwordHash);

                return speler;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


   /* @Override
    public void changeCredentials(String username, String passwdHash) throws UsernameAlreadyInUseException {
        String sql = "INSERT INTO Users(passwdHash) Where username=username VALUES(?)";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passwdHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }*/

    private synchronized void updateTime(String username) {
        String sql = "UPDATE Users SET timestamptoken = ? WHERE username = ?";

     /*   cv.put("LastModifiedTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));*/
        //   String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String time = new DateTime().toString();

        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, time);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }


    }

   /* public static Set<String> getUserSet(){
        Set<String> userlijst = new HashSet<String>();

        String sql = "SELECT * FROM Users";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                userlijst.add(rs.getString("username"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userlijst;
    }*/

    /** Methode die spelerpunten in lijst teruggeeft van bepaalde gameid
     * @param gameid String met waarde gameid
     * @return Lijst van integers van de punten van alle verschillende spelers van de game
     *
     * */
    @Override
    public synchronized ArrayList<Integer> getSpelerPunten(String gameid) {
        //   HashMap<Integer,Integer> scores = new HashMap<>();
        ArrayList<Integer> scores = new ArrayList<>();
        String sql = "SELECT * FROM GameSpelertable WHERE gameid = ?;";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gameid);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userid = rs.getInt("userid");
                int spelerpunten = rs.getInt("spelerpunten");
                scores.add(spelerpunten);
                // scores.put(userid,spelerpunten);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }

    //zet started state van game met gameId op par:b

    /** Methode die een game start met een gameid
     * @param gameId String met waarde gameid
     * @param b boolean die zegt of spel gestart is
     *
     * */
    @Override
    public synchronized void setStarted(boolean b, String gameId) {
        String sql = "UPDATE Game SET started = ? WHERE gameid = ?";
        try (Connection conn = connect(databankstring);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, b);
            pstmt.setString(2, gameId);
            pstmt.executeUpdate();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
    /** Methode setStarted doorgeeft rond verschillende databanken
     * @param gameId String met waarde gameid
     * @param b boolean die zegt of spel gestart is
     * @param eindid int van laatste databank kring
     *
     * */
    public synchronized void floodSetStarted(boolean b, String gameId, int eindid){
        setStarted(b, gameId);
        if(eindid!=peer.getId()){              //TODO goed eine setten
            try {
                implDBvolgende.floodSetStarted(b, gameId , eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }






    ////////////////////////////////////////////
    ////////////////////////////////////////////
    ////////////////////////////////////////////
    /////////////////////////////////////////////
    //RINGSTRUCTUUR


    public synchronized void toevoegRing(Node successor){
        //NOG GEEN SUCCESSOR, EERSTE ELEMENT
        if(successor == null){
            node.setId(0);
            node.create();
        }
        else{
            node.setId(successor.getId() + 1);
            node.join(successor);
        }


     /*   if(successorid==-1){
            node.create();
        }

        else{
            node.join();
        }
*/
    }


    @Override
    public synchronized void toevoegNode(Node predecessor){
        if(predecessor == null){

            this.node.setId(0);
            this.node.setPredecessor(this.getNode());
            this.node.setSuccessor(this.getNode());
         //   this.node.create();

        }
        else{
            this.node.setId(predecessor.getId() + 1);
            this.node.setPredecessor(predecessor);
            //this.node.toevoeg(predecessor);
            predecessor.setSuccessor(this.getNode());
        }

    }


    @Override
    public synchronized Node getNode() {
        return node;
    }

    @Override
    public synchronized void setNode(Node node) {
        this.node = node;
    }



    /** Methode die databank server toevoegd aan architectuur (ring)
     *
     * */
    @Override
    public synchronized void toevoegPeer(Peer predecessor){
        if(predecessor == null){
            this.peer.setId(0);
            this.peer.setPredecessor(peer.getId());
            this.peer.setSuccessor(peer.getId());

            //   this.node.create();
        }
        else{
            this.peer.setId(predecessor.getId() + 1);
            this.peer.setPredecessor(predecessor.getId());
            //this.node.toevoeg(predecessor);
            predecessor.setSuccessor(this.peer.getId());
        }

    }


    @Override
    public synchronized void toevoegSuccesor(int portid){


            this.peer.setSuccessor(portid);

            try {
                implDBvolgende.connectSuccessor(portid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


  /*  @Override
    public synchronized void toevoegSuccesor(int portid){
        if(successorid == -1){
            this.successorid = id;

        }
        else{
            this.successorid = portid;

            try {
                implDBvolgende.connectSuccessor(portid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }*/



    /** Methode die Databankserver connecteert met databank van bepaalde port
     * @param port poort waarbij databank moet geconecteerd worden
     *
     * */
    public synchronized void connectSuccessor(int port) throws RemoteException {

        Registry registryServer = LocateRegistry.getRegistry("localhost", port);
        try {
            implDBvolgende = (rmi_int_appserver_db) registryServer.lookup("DVolgendeImplService");
            System.out.println("SUCCESOR CONNECTION GEMAAKT");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Peer getPeer() {
        return peer;
    }

    @Override
    public synchronized void setPeer(Peer peer) {
        this.peer = peer;
    }

    @Override
    public synchronized void setSuccessor(Peer peer) {
        this.peer.setSuccessor(peer.getId());
    }

    @Override
    public synchronized void setPredecessor(Peer peer) {
        this.peer.setPredecessor(peer.getId());
    }

    @Override
    public synchronized void setPeer(int id){
        this.peer.setId(id);
    }








}
