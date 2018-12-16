package application_server;

import application_server.memory_spel.Game;
import application_server.memory_spel.Speler;
import dispatcher.DbInfo;
import exceptions.NoServerAvailableException;
import exceptions.NoValidTokenException;
import shared_client_appserver_stuff.rmi_int_client_appserver;
import shared_client_appserver_stuff.rmi_int_client_appserver_updater;
import shared_db_appserver_stuff.rmi_int_appserver_db;
import shared_dispatcher_appserver_client_stuff.rmi_int_dispatcher_appserver_client;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static client.ClientMainGUI.dispatcherImpl;


public class ServerMain {
    public static rmi_int_dispatcher_appserver_client dispatcherImpl;

    protected static final int PORT_SERVER = 10001;
    public static final String ADDRESS_SERVER = "localhost";
    public static int serverId;
    private static final String ADDRESS_DISPATCHER = "localhost";
    private static final int PORT_DISPATCHER = 12345;
    public static String ADDRESSDB;
    public static int PORTDB;
    public static int ID_DB;
    public static rmi_int_appserver_db dbImpl;

    public static final int MEAN_GAME_NUMBER = 4;//15;
    public static final int MAX_GAME_NUMBER = 6; //20;
    public static final int MIN_GAME_NUMBER = 2; //4;


    //aangemelde clients waarnaar bord/lobby updates moet sturen
    //key is username
    public static Map<String, rmi_int_client_appserver_updater> clients;
    public static Map<String, Speler> spelers;
    //lijst van usernames per gameId, users die update moeten krijgen van game
    public static Map<String, List<String>> gameUpdateSubscribers;

    //lijst van games die momenteel open staan op server
    //games die gecached zijn op server
    //gameId is key
    public static Map<String, Game> activeGames;

    //games waarvan deze appserver al kent, zodat kan tonen in lobby (update op tijdsbasis)
    public static Map<String, Game> knownGames;

    private void startServer() throws RemoteException, NotBoundException {
        //registreer bij de dispatcher
        Registry registryDispatcher = LocateRegistry.getRegistry(ADDRESS_DISPATCHER, PORT_DISPATCHER);
        dispatcherImpl = (rmi_int_dispatcher_appserver_client) registryDispatcher.lookup("DispatcherImplService");
        serverId = dispatcherImpl.registerAppServer(ADDRESS_SERVER, PORT_SERVER);

        //doe de initialisatie
        clients = new HashMap<>();
        gameUpdateSubscribers = new HashMap<>();
        knownGames = new HashMap<>();
        activeGames = new HashMap<>();
        spelers = new HashMap<>();
        try {
            Registry registry = LocateRegistry.createRegistry(PORT_SERVER);
            registry.rebind("ServerImplService", new ServerImpl());
            System.out.println("System is ready");
            System.out.println("ServerId: " + serverId);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        try {
            ADDRESSDB = args[0];
            PORTDB = Integer.parseInt(args[1]);
            ID_DB = Integer.parseInt(args[2]);
            ServerMain main = new ServerMain();
            main.startServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void renewDbServer() throws NoServerAvailableException {
        try {
            DbInfo dbInfo = dispatcherImpl.reportBadDbServer(ID_DB);

            setIdDb(dbInfo.getDbId());
            setPortDb(dbInfo.getPort());
            setAddressDb(dbInfo.getAddress());
            System.out.println("Nieuwe db: " + ID_DB);

            try {
                Registry registryServer = LocateRegistry.getRegistry(ADDRESSDB, PORTDB);
                dbImpl = (rmi_int_appserver_db) registryServer.lookup("DbServerImplService");
                System.out.println("DB connection ok");
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (ConnectException ce) {
                renewDbServer();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void setAddressDb(String addressDb) {
        ADDRESSDB = addressDb;
    }

    public static synchronized void setPortDb(int portDb) {
        PORTDB = portDb;
    }

    public static synchronized void setIdDb(int id) {
        ID_DB = id;
    }


}
