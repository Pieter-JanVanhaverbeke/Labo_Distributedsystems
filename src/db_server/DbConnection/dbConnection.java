package db_server.DbConnection;


import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class dbConnection {


        public static Connection connect(String databank){
            Connection c = null;

            try {
                Class.forName("org.sqlite.JDBC");
               // c = DriverManager.getConnection("jdbc:sqlite:memory.db");
                c = DriverManager.getConnection(databank);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
         //   System.out.println("Opened database successfully");
            return c;
        }

    public static Set<String> getUserSet(String databank){
        Set<String> userlijst = new HashSet<String>();

        String sql = "SELECT username FROM Users";

        try (Connection conn = connect(databank);
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
