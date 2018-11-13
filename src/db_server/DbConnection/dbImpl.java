package db_server.DbConnection;

import application_server.memory_spel.*;
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
    public String createUser(String username, String password) throws UsernameAlreadyInUseException {
        String time =  new DateTime().toString();       //huidige tijd dat je plaatst in DB


        if (dbConnection.getUserSet().contains(username)) {
            System.out.println("gebruikersnaam: " + username + " al gebruikt");
            throw new UsernameAlreadyInUseException(username);
        } else {
            String sql = "INSERT INTO Users(username,password,globalScore,token,timestamptoken,lobbyid) VALUES(?,?,?,?,?,?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setInt(3,0);
                pstmt.setString(4,"token");     //nog aanpassen naar random token
                pstmt.setString(5,time);
                pstmt.setInt(6,1);              //naar eerste en enigste lobby zetten
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


         //   Speler maarten = getSpeler("PJ");
     //       System.out.println("token Maarten: " + validateUsertoken("Maarten"));

            //Wachtwoord Hashen en naar databank sturen(bij de client hashen)


            //  dbConnection.insert(username,passwdHash);
        /*    String token = Utils.generateUserToken(username);
            Speler speler = new Speler(username);
            userTokens.put(token, speler); //wordt al op app server bijgehouden
            System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
            */
            return "token";
        }
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

    /*@Override
    public boolean validateUsertoken(Speler speler) {
        String username = speler.getUsername();
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (
            Connection conn = connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql)){

                // set the values
                pstmt.setString(1, username);
                //
                ResultSet rs = pstmt.executeQuery();




          //  ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String timestamptoken = rs.getString("timestamptoken");

                DateTime tijdtoken = DateTime.parse(timestamptoken);
                System.out.println("tijd user: " + tijdtoken.toString());

                DateTime dateTime = new DateTime();
                dateTime = dateTime.minusDays(1);    //dateTime.plusDays(1);
                System.out.println("tijd valid: " + dateTime.toString());

                if (tijdtoken.compareTo(dateTime) > 0) {
                    return true;
                } else return false;

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; //als niets zou vinden, ook false returnen
    }*/

    @Override
    public void addGame(String creator, String createdate, boolean started, int aantalspelers, int bordgrootte, int layout) {

        Connection conn = connect();

        String sql = "INSERT INTO Game(creator,createdate,started,aantalspelers,bordgrootte,layout) VALUES(?,?,?,?,?,?)";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, creator);
            pstmt.setString(2,createdate);
            pstmt.setBoolean(3,started);
            pstmt.setInt(4,aantalspelers);
            pstmt.setInt(5,bordgrootte);
            pstmt.setInt(6,layout);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGame(int gameId){
        String sql = "DELETE FROM Game WHERE gameid=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void addSpelerToGame(int userid, int gameid){
        String sql = "INSERT INTO GameSpelertable(userid,gameid) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userid);
            pstmt.setInt(2, gameid);
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


  /*  @Override
    public void addNewBordspel(int layout, int grootte) {
        String sql = "INSERT INTO GameSpelertable(layout,grootte) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, layout);
            pstmt.setInt(2, grootte);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
*/

  /*  @Override
    public void addKaart(int xpos, int ypos, int bordspelid) {
        String sql = "INSERT INTO GameSpelertable(layout,grootte,bordspelid,faceup) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, xpos);
            pstmt.setInt(2, ypos);
            pstmt.setInt(3,bordspelid);
            pstmt.setBoolean(4,false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
*/

 /*   public void flipKaart(int kaartid, boolean faceup){
        String sql = "UPDATE Kaart SET faceup = ? WHERE kaartid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kaartid);
            pstmt.setBoolean(2,faceup);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

*/



        //////////////////////////////////// HaalDatabase ///////////////////////////////////////////

    @Override //return lege lijst als geen games
    public Map<Integer, Game> getAllGames() { //return alle games in db met gameId = key
        Map<Integer, Game> map = new HashMap<Integer, Game>();
        int teller = 0;
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
                String bordspelfacup = rs.getString("bordspelfacup");
                int layout = rs.getInt("layout");

                String[] valuestypes = bordspeltypes.split("\\s+");
                String[] valuefacup = bordspelfacup.split("\\s+");


                int size = 2*bordgrootte+2;
                Bordspel bordspel = new Bordspel(size,size);          //size meegeven
                Kaart bordspelkaarten[][] = new Kaart[size][size];

                //alle gegevens naar kaarten brengen
                for(int i=0; i<valuestypes.length; i++){

                        Kaart kaart = new Kaart();
                        int soort = Integer.parseInt(valuestypes[i]);
                        boolean faceup = Boolean.valueOf(valuefacup[i]);
                        kaart.setSoort(soort);
                        kaart.setFaceUp(faceup);
                        bordspelkaarten[i/4][i%4] = kaart;                  //naar matrix omzetten


                    }
                //alle gegevens naar bordspel steken
                bordspel.setBord(bordspelkaarten);
                bordspel.setType(layout);

                //alles in game steken
                Game game = new Game(bordgrootte,gameid,aantalspelers,creator);
                game.setCreateDate(createdate);
                game.setStarted(started);
                game.setBordspel(bordspel);

                map.put(teller,game);
                teller++;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
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
                String passwordHash = rs.getString("passwordHash");
                int globalScore = rs.getInt("globalScore");

                Speler speler = new Speler(username);
                speler.setSpelerId(spelerId);
                speler.setGlobalScore(globalScore);
                speler.setPasswordHash(passwordHash);
                spelerslijst.add(speler);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return spelerslijst;
    }

    @Override
    public Speler getSpeler(String username) {
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Users WHERE username=username;";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int spelerId = rs.getInt("spelerId");
                String passwordHash = rs.getString("password");
                int globalScore = rs.getInt("globalScore");


                Speler speler = new Speler(username);
                speler.setSpelerId(spelerId);
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

 /*   @Override
    public Lobby getLobby() {
        Lobby lobby = null;
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Lobby";
            ResultSet rs = stmt.executeQuery(sql);
                 while (rs.next()) {
                      lobby = new Lobby();
                 }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lobby;
    }
*/
 /*   @Override
    public void persistLobby(Lobby lobby) {
        String dummy = "dummy";
        String sql = "INSERT INTO Lobby(dummy) VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dummy);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
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


}
