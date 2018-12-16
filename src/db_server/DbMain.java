package db_server;

import db_server.DbConnection.dbImpl;
import shared_dispatcher_appserver_client_stuff.rmi_int_dispatcher_appserver_client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by ruben on 1/11/18.
 */
public class DbMain {
    public static String databankstring = "jdbc:sqlite:memory1.db";

    public static rmi_int_dispatcher_appserver_client dispatcherImpl;

    private static final String ADDRESS_DB = "localhost";
    private static final int PORT_DB = 20000;
    private static int DB_ID;
    private static final String ADDRESS_DISPATCHER = "localhost";
    private static final int PORT_DISPATCHER = 12345;


    //hier komt de main voor de db server
    private void startDB() throws RemoteException, NotBoundException {
        //registreer bij de dispatcher
        Registry registryDispatcher = LocateRegistry.getRegistry(ADDRESS_DISPATCHER, PORT_DISPATCHER);
        dispatcherImpl = (rmi_int_dispatcher_appserver_client) registryDispatcher.lookup("DispatcherImplService");
        DB_ID = dispatcherImpl.registerDBServer(ADDRESS_DB, PORT_DB);

        try {
            Registry registry = LocateRegistry.createRegistry(PORT_DB);
            registry.rebind("DbServerImplService", new dbImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Databasesysteem is ready");
        System.out.println("id: " + DB_ID + ", port: " + PORT_DB);
    }

    public static void main(String[] args) {
        try {
            DbMain main = new DbMain();
            main.startDB();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }


}
