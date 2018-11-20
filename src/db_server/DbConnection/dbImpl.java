package db_server.DbConnection;

import application_server.memory_spel.*;
import exceptions.PlayerNumberExceededException;
import exceptions.UserDoesNotExistException;
import shared_db_appserver_stuff.rmi_int_appserver_db;
import exceptions.UsernameAlreadyInUseException;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

import static db_server.DbConnection.dbConnection.connect;

public class dbImpl extends UnicastRemoteObject implements rmi_int_appserver_db, Serializable {
    private HashMap<String, Speler> userTokens;//bevat de huidig uitgeleende tokens ( = aangemelde users)


    public dbImpl() throws RemoteException {
        userTokens = new HashMap<>();
    }


    @Override
    public int createUser(String username, String passwordHash, String salt) throws UsernameAlreadyInUseException {
        int id =-1;

        if (dbConnection.getUserSet().contains(username)) {
            System.out.println("gebruikersnaam: " + username + " al gebruikt");
            throw new UsernameAlreadyInUseException(username);
        } else {
            String sql = "INSERT INTO Users(username,password,globalScore,salt) VALUES(?,?,?,?)";
            Connection conn = connect();

            try (PreparedStatement pstmt = conn.prepareStatement(sql))
            {
                pstmt.setString(1, username);
                pstmt.setString(2, passwordHash);
                pstmt.setInt(3,0);
                pstmt.setString(4,salt);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try (
                    PreparedStatement psmt = conn.prepareStatement("SELECT last_insert_rowid() AS NewID;")) {
                ResultSet resultSet2 = psmt.executeQuery();
                while(resultSet2.next()) {
                    id = resultSet2.getInt("NewID");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            return id;
        }
    }

    @Override
    public String getSalt(String username) throws UserDoesNotExistException {
        String salt = "";
        String sql = "SELECT * FROM Users WHERE username = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                salt = rs.getString("salt");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //if user niet bestaat => throw UserDoesNotExistException
        if(salt.equals("")){
            throw new UserDoesNotExistException("user does not exist");
        }

        return salt;
    }

    @Override
    public void setUsertoken(Speler speler, String token) {
        String username = speler.getUsername();
        String sql = "UPDATE Users SET token = ? , WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        updateTime(username);           //setten van tijd usertoken
    }


    @Override
    public int createGame(String creator, String createdate, boolean started, int aantalspelers, int bordgrootte, int layout, String bordspeltypes, String bordspelfaceup) {

        int id = -1;
        String sql = "INSERT INTO Game(creator,createdate,started,aantalspelers,bordgrootte,layout,bordspeltypes,bordspelfaceup,spelersbeurt)VALUES(?,?,?,?,?,?,?,?,?)";

        Connection conn = connect();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, creator);
            pstmt.setString(2, createdate);
            pstmt.setBoolean(3, started);
            pstmt.setInt(4, aantalspelers);
            pstmt.setInt(5, bordgrootte);
            pstmt.setInt(6, layout);
            pstmt.setString(7, bordspeltypes);
            pstmt.setString(8, bordspelfaceup);
            pstmt.setInt(9,0);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement psmt = conn.prepareStatement("SELECT last_insert_rowid() AS NewID;")) {
            ResultSet resultSet2 = psmt.executeQuery();
            while(resultSet2.next()) {
                id = resultSet2.getInt("NewID");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        return id;

    }

    @Override
    public void deleteGame(int gameId) {
        String sql = "DELETE FROM Game WHERE gameid=?";
        Connection conn = connect();
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql2 = "DELETE FROM GameSpelertable WHERE gameid=?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

        @Override
    public void updateFaceUp(int gameid,String data){
        String sql = "UPDATE  Game SET bordspelfaceup = ? WHERE gameid=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,data);
            pstmt.setInt(2, gameid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addSpelerToGame(int userid, int gameid){
        String sql = "INSERT INTO GameSpelertable(userid,gameid,spelerpunten) VALUES(?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userid);
            pstmt.setInt(2, gameid);
            pstmt.setInt(3,0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void removeSpelerToGame(int userid, int gameid){
        String sql = "DELETE FROM GameSpelertable WHERE userid = ? AND gameid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userid);
            pstmt.setInt(2, gameid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePunten(int gameid, int userid, int punten){
        String sql = "UPDATE GameSpelertable SET spelerpunten = ? WHERE gameid = ? AND userid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, punten);
            pstmt.setInt(2, gameid);
            pstmt.setInt(3,userid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateGlobalScore(int spelerid, int punten){
        String sql = "UPDATE Users SET globalScore = ? WHERE spelerid = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, punten);
            pstmt.setInt(2, spelerid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSpelersbeurt(int gameid, int spelersbeurt){
        String sql = "UPDATE Game SET spelersbeurt = ? WHERE gameid = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("gameid van spel: " + gameid);
            System.out.println("spelerbeurt: " + spelersbeurt);
            pstmt.setInt(1, spelersbeurt);
            pstmt.setInt(2, gameid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //////////////////////////////////// HaalDatabase ///////////////////////////////////////////

    @Override //return lege lijst als geen games
    public Map<Integer, Game> getAllGames() { //return alle games in db met gameId = key
        Map<Integer, Game> map = new HashMap<Integer, Game>();
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Game";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {                         //voor alle games

                int gameid = rs.getInt("gameid");
                String creator = rs.getString("creator");
                String createdate = rs.getString("createdate");
                boolean started = rs.getBoolean("started");
                int bordgrootte = rs.getInt("bordgrootte");
                int aantalspelers = rs.getInt("aantalspelers");
                String bordspeltypes = rs.getString("bordspeltypes");
                String bordspelfacup = rs.getString("bordspelfaceup");
                int layout = rs.getInt("layout");
                int spelersbeurt = rs.getInt("spelersbeurt");

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


    @Override
    public String getFaceUp(int gameid){
            String faceup = "";
            String sql = "SELECT * FROM Game WHERE gameid = ?;";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, gameid);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    faceup = rs.getString("bordspelfaceup");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        return faceup;
    }

        @Override
    public List<Speler> getAllSpelers() {

        List<Speler> spelerslijst = new ArrayList<Speler>();

        try {
            Connection conn = connect();
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



    @Override
    public List<Integer> getAlleSpelerid (int gameid) {
        ArrayList<Integer> speleridlijst = new ArrayList<Integer>();


        String sql = "SELECT userid FROM GameSpelertable WHERE gameid = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameid);

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

    @Override
    public Speler getSpeler(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs  = pstmt.executeQuery();

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


    @Override
    public Speler getSpeler(int spelerid) {
        String sql = "SELECT * FROM Users WHERE spelerid = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, spelerid);

            ResultSet rs  = pstmt.executeQuery();

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





    @Override
    public void changeCredentials(String username, String passwdHash) throws UsernameAlreadyInUseException {
        String sql = "INSERT INTO Users(passwdHash) Where username=username VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passwdHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    private void updateTime(String username) {
        String sql = "UPDATE Users SET timestamptoken = ? WHERE username = ?";

     /*   cv.put("LastModifiedTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));*/
        //   String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String time =  new DateTime().toString();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, time);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }

        catch(
                SQLException e)

        {
            e.printStackTrace();
        }


    }

    public static Set<String> getUserSet(){
        Set<String> userlijst = new HashSet<String>();

        String sql = "SELECT * FROM Users";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                userlijst.add(rs.getString("username"));
            }



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userlijst;
    }

    @Override
    public ArrayList<Integer> getSpelerPunten(int gameid) {
        //   HashMap<Integer,Integer> scores = new HashMap<>();
        ArrayList<Integer> scores = new ArrayList<>();
        String sql = "SELECT * FROM GameSpelertable WHERE gameid = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameid);

            ResultSet rs  = pstmt.executeQuery();

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
    @Override
    public void setStarted(boolean b, int gameId) {
        String sql = "UPDATE Game SET started = ? WHERE gameid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, b);
            pstmt.setInt(2, gameId);
            pstmt.executeUpdate();
        }
        catch(
                SQLException e)

        {
            e.printStackTrace();
        }
    }


}
