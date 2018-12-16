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
    private static final int PORTDB1 = 13001;
    private static final int PORTDB2 = 14001;
    private static final int PORTDB3 = 15001;
    private static final int PORTDB4 = 16001;
    //hier komt de main voor de db server
    public void startDB(int port) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("DbServerImplService", new dbImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Databasesysteem is ready");
    }


    public static void main(String[] args) {
        DbMain main = new DbMain();

        main.startDB(PORTDB1);
        main.startDB(PORTDB2);
        main.startDB(PORTDB3);
        main.startDB(PORTDB4);

    }


}
