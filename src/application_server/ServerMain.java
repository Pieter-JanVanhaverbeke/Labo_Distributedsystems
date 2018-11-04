package application_server;

import db_server.DbConnection.dbImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    private void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(10001);
            registry.rebind("ServerImplService", new ServerImpl());
            registry.rebind("DatabaseImplService", new dbImpl()); //moet dit niet in db-server staan?


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public static void main(String[] args) {
        ServerMain main = new ServerMain();
        main.startServer();

        //TODO: db
        /*dbConnection.connect();
        dbConnection.selectAll();
        Set<String> set = dbConnection.getUserSet();

        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }*/

    }
}
