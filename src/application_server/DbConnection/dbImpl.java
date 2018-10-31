package application_server.DbConnection;

import application_server.Utils.Utils;
import application_server.memory_spel.Lobby;
import application_server.memory_spel.Speler;
import appserver_db.rmi_int_appserver_db;
import exceptions.UsernameAlreadyInUseException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

import static application_server.DbConnection.dbConnection.connect;

public class dbImpl extends UnicastRemoteObject implements rmi_int_appserver_db {
    private HashMap<String, Speler> userTokens = new HashMap<>(); //bevat de huidig uitgeleende tokens ( = aangemelde users)

    private Lobby lobby;


    public dbImpl() throws RemoteException {

    }

    @Override
    public String createUser(String username, String passwdHash) throws UsernameAlreadyInUseException {
        if(dbConnection.getUserSet().contains(username)){
            System.out.println("gebruikersnaam al gebruikt");
            throw new UsernameAlreadyInUseException();
        }

        //Wachtwoord Hashen en naar databank sturen(bij de client hashen)

        dbConnection.insert(username,passwdHash);
        String token = Utils.generateUserToken(username);
        Speler speler = new Speler(username);
        userTokens.put(token, speler);
        System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
        return token;
    }

    @Override
    public void setUsertoken(Speler speler, String token) {
        String sql = "INSERT INTO Users(token) VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void invalidateUsertoken(Speler speler) {

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

                int spelerId  = rs.getInt("spelerId");
                String username = rs.getString("username");
                String passwordHash = rs.getString("passwordHash");
                int globalScore = rs.getInt("globalScore");

                Speler speler = new Speler(username);
                speler.setSpelerId(spelerId);
                speler.setGlobalScore(globalScore);
                speler.setPasswordHash(passwordHash);
                spelerslijst.add(speler);
            }

        }

        catch (SQLException e) {
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
        }
        catch (SQLException e) {
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
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
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
