package db_server.DbConnection;

import application_server.Utils.Utils;
import application_server.memory_spel.Speler;
import shared_db_appserver_stuff.rmi_int_appserver_db;
import exceptions.UsernameAlreadyInUseException;
import org.joda.time.DateTime;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

import static db_server.DbConnection.dbConnection.connect;

public class dbImpl extends UnicastRemoteObject implements rmi_int_appserver_db {
    private HashMap<String, Speler> userTokens = new HashMap<>(); //bevat de huidig uitgeleende tokens ( = aangemelde users)


    //  private Lobby lobby;

    public dbImpl() throws RemoteException {
        // this.lobby = new Lobby();

    }

    @Override
    public String createUser(String username, String passwdHash) throws UsernameAlreadyInUseException {
        if (dbConnection.getUserSet().contains(username)) {
            System.out.println("gebruikersnaam: " + username + " al gebruikt");
            throw new UsernameAlreadyInUseException(username);
        }

        //Wachtwoord Hashen en naar databank sturen(bij de client hashen)

        //  dbConnection.insert(username,passwdHash);
        String token = Utils.generateUserToken(username);
        Speler speler = new Speler(username);
        userTokens.put(token, speler); //wordt al op app server bijgehouden
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        return token;
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
    public boolean validateUsertoken(Speler speler) {
        String username = speler.getUsername();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Users WHERE username=username;";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String timestamptoken = rs.getString("timestamptoken");

                DateTime tijdtoken = DateTime.parse(timestamptoken);

                DateTime dateTime = new DateTime();
                dateTime = dateTime.minusDays(1);
                if(tijdtoken.compareTo(dateTime)>0){
                    return true;
                }
                else return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; //als niets zou vinden, ook false returnen
    }

        //////////////////////////////////// HaalDatabase ///////////////////////////////////////////

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
                String passwordHash = rs.getString("passwordHash");
                int globalScore = rs.getInt("globalScore");


                Speler speler = new Speler(username);
                speler.setSpelerId(spelerId);
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


}
