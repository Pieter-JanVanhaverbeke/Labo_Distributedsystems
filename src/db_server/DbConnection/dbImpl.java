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

public class dbImpl extends UnicastRemoteObject implements rmi_int_appserver_db, Serializable {
    private HashMap<String, Speler> userTokens;//bevat de huidig uitgeleende tokens ( = aangemelde users)
    private Node node;
    private Peer peer;
    String databankstring = "jdbc:sqlite:memory.db";
    private rmi_int_appserver_db implDBvolgende;


    public dbImpl() throws RemoteException {
        userTokens = new HashMap<>();
     //   node = new Node();
        peer = new Peer();



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
     *
     * */
    @Override
    public int createUser(String username, String passwordHash, String salt) throws UsernameAlreadyInUseException {
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
     * */
    @Override
    public void floodCreateUser(String username,String passwordHash, String salt, int eindid){
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



    /** Methode om de globale score aan te passen van een speler in een databank */
    public void updateGlobalScore(int spelerid, int punten){
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



    /** Methode om de methode updateGlobalScore toe te passen rond elke databank*/
    @Override
    public void floodUpdateGlobalScore(int spelerid, int punten, int eindid){

        updateGlobalScore( spelerid,  punten);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodUpdateGlobalScore(spelerid, punten, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /** Methode die als ingegeven argument de gebruiker de salt teruggeeft van de gebruiker waarvoor gehashed werd*/
    @Override
    public String getSalt(String username) throws UserDoesNotExistException {
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
    /** Methode om een game te creëren in een databank, return type is int en geeft de primary key terug*/
    @Override
    public String createGame(String creator, String createdate, boolean started, int aantalspelers, int bordgrootte, int layout, String bordspeltypes, String bordspelfaceup, String serverIp, int serverPort, int serverId) {

        String id = null;
        String sql = "INSERT INTO Game(creator,createdate,started,aantalspelers,bordgrootte,layout,bordspeltypes,bordspelfaceup,spelersbeurt, serverip, serverport, serverid)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

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
            pstmt.setInt(9,0);
            System.out.println(System.currentTimeMillis());
            pstmt.setString(10, String.valueOf(System.currentTimeMillis()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement psmt = conn.prepareStatement("SELECT last_insert_rowid() AS NewID;")) {
            ResultSet resultSet2 = psmt.executeQuery();
            while (resultSet2.next()) {
                id = resultSet2.getString("NewID");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        return id;

    }

    /** Methode om een spel te verwijderen uit een databank met key gameId */
    @Override
    public void deleteGame(String gameId) {
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
    /** Methode die rondvraagd aan alle databanken om game te deleten */
    @Override
    public void floodDeleteGame(String gameId, int eindid){//TODO NOG TESTEN
        deleteGame(gameId);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodDeleteGame(gameId, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /** Methode de faceup logica aan te passen aan een game*/
        @Override
    public void updateFaceUp(String gameid,String data){
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


    @Override
    public void addSpelerToGame(int userid, String gameid){
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
    /** Methode om een speler met userid te verwijderen van een game met bepaalde gameid*/
    @Override
    public void removeSpelerToGame(int userid, String gameid){
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


    /** Methode om een removeSpelerToGame toe te passen rond alle databanken */
    @Override
    public void floodRemoveSpelerToGame(int userid, String gameid, int eindid){
        removeSpelerToGame(userid, gameid);                                     //TODO NIET OVER ALLE DB GAAN
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodRemoveSpelerToGame(userid, gameid, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /** Methode om de punten te updaten van een spel in de databank*/
    public void updatePunten(String gameid, int userid, int punten){
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

    /** Methode die updatePunten doorgeeft naar alle databanken */
    public void floodUpdatePunten(String gameid, int userid, int punten, int eindid){      //TODO NIET ALLES CHECKEN?
        updatePunten( gameid,  userid, punten);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodUpdatePunten(gameid, userid, punten, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    /** Methode die een spelersbeurt update van game met gameid*/
    public void updateSpelersbeurt(String gameid, int spelersbeurt){
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

    /** Methode die updateSpelersbeurt doorgeeft aan verschillende databanken */
    public void floodUpdateSpelersbeurt(String gameid, int spelersbeurt, int eindid){
        updateSpelersbeurt( gameid, spelersbeurt);
        if(eindid!=peer.getId()){
            try {
                implDBvolgende.floodUpdateSpelersbeurt(gameid, spelersbeurt, eindid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //zet game in db, game bestaat al maar dit is een full update voordat een server shutdownt
    @Override
    public void fullUpdate(Game game) {

    }


    //////////////////////////////////// HaalDatabase ///////////////////////////////////////////

    /** Methode die alle games opvraagd van een databank, return type is map met primary key gameid en value de games*/
    @Override //return lege lijst als geen games
    public Map<String, Game> getAllGames() { //return alle games in db met gameId = key
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

    /** Methode die GetAlleGames rondvraagt aan alle databanken, return type is map met primary key gameid en value de games */
    @Override
    public Map<String, Game> floodGetAlleGames(Map<String, Game> gamesmap, int eindid, int teller) {   //TODO OPLOSSEN GAMES TERUGGEVEN

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


    @Override
    public Game getGame(String gameId) {
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



    /** Methode die info van omgedraaide kaartjes teruggeeft van game met gameid, return type is String die bestaat uit 0 en 1'en*/
    @Override
    public String getFaceUp(String gameid){
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
    /** Methode die getFaceUp rondvraagt aan alle databanken,  return type is String die bestaat uit 0 en 1'en */
    public String floodGetFaceUp(String gameid, String faceup, int eindid, int teller){
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

    /** Methode die alle spelers opvraagd aan databank, return type is lijst van de spelers */
    @Override
    public List<Speler> getAllSpelers() {

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


    /** Methode die alle spelersids opvraagd van een bepaalde game met gameid, returntype is lijst van spelerids */
    @Override
    public List<Integer> getAlleSpelerid (String gameid) {
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


    /** Methode die een speler teruggeeft met String username*/
    @Override
    public Speler getSpeler(String username) {
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


    /** Methode die speler ophaald met een spelerid*/
    @Override
    public Speler getSpeler(int spelerid) {
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

    private void updateTime(String username) {
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

    /** Methode die spelerpunten in lijst teruggeeft van bepaalde gameid*/
    @Override
    public ArrayList<Integer> getSpelerPunten(String gameid) {
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

    /** Methode die een game start met een gameid*/
    @Override
    public void setStarted(boolean b, String gameId) {
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
    /** Methode setStarted doorgeeft rond verschillende databanken*/
    public void floodSetStarted(boolean b, String gameId, int eindid){
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


    public void toevoegRing(Node successor){
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
    public void toevoegNode(Node predecessor){
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
    public Node getNode() {
        return node;
    }

    @Override
    public void setNode(Node node) {
        this.node = node;
    }



    /** Methode die databank server toevoegd aan architectuur (ring)*/
    @Override
    public void toevoegPeer(Peer predecessor){
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
    /** Methode die Databankserver connecteert met databank van bepaalde port*/
    public void connectSuccessor(int port) throws RemoteException {

        Registry registryServer = LocateRegistry.getRegistry("localhost", port);
        try {
            implDBvolgende = (rmi_int_appserver_db) registryServer.lookup("DbServerImplService");
            System.out.println("SUCCESOR CONNECTION GEMAAKT");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Peer getPeer() {
        return peer;
    }

    @Override
    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void setSuccessor(Peer peer) {
        this.peer.setSuccessor(peer.getId());
    }

    @Override
    public void setPredecessor(Peer peer) {
        this.peer.setPredecessor(peer.getId());
    }

    public String getDatabankstring() {
        return databankstring;
    }

    public void setDatabankstring(String databankstring) {
        this.databankstring = databankstring;
    }







}
