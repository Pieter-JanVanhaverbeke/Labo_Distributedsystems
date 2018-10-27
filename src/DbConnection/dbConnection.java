package DbConnection;


import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class dbConnection {


        public static Connection connect(){
            Connection c = null;

            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:memory.db");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Opened database successfully");
            return c;
        }

        public static void insert(String username1, String password1) {
            String sql = "INSERT INTO Users(username,password) VALUES(?,?)";
            // String sql = "INSERT INTO Users(username,password) VALUES(PJ,PJ)";

            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username1);
                pstmt.setString(2, password1);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    public static void selectAll(){
        String sql = "SELECT username, password FROM Users";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("username") + "\t" +
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static Set<String> getUserSet(){
        Set<String> userlijst = new HashSet<String>();

        String sql = "SELECT username FROM Users";

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
    /**
     * Connect to a sample database
     */
