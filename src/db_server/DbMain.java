package db_server;

import application_server.ServerImpl;
import application_server.ServerMain;
import db_server.DbConnection.dbImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by ruben on 1/11/18.
 */
public class DbMain {
    //hier komt de main voor de db server
    private void startDB() {
        try {
            Registry registry = LocateRegistry.createRegistry(13001);
            registry.rebind("DbServerImplService", new dbImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Databasesysteem is ready");
    }

    public static void main(String[] args) {
        DbMain main = new DbMain();
        main.startDB();

    }


}
