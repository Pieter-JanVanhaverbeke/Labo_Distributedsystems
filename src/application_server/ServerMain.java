package application_server;

import shared_client_appserver_stuff.rmi_int_client_appserver_updater;
import shared_dispatcher_appserver_client_stuff.rmi_int_dispatcher_appserver_client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerMain {
    public static rmi_int_dispatcher_appserver_client dispatcherImpl;

    protected static final int PORT_SERVER = 10001;
    public static final String ADDRESS_SERVER = "localhost";
    private static int serverId;
    private static final String ADDRESS_DISPATCHER = "localhost";
    private static final int PORT_DISPATCHER = 12345;

    //clients waarnaar bord/lobby updates moet sturen
    //key is username
    public static Map<String, rmi_int_client_appserver_updater> clients;
    //lijst van usernames per gameId, users die update moeten krijgen van game
    public static Map<Integer, List<String>> gameUpdateSubscribers;


    private void startServer() throws RemoteException, NotBoundException {
        //registreer bij de dispatcher
        Registry registryDispatcher = LocateRegistry.getRegistry(ADDRESS_DISPATCHER, PORT_DISPATCHER);
        dispatcherImpl = (rmi_int_dispatcher_appserver_client) registryDispatcher.lookup("DispatcherImplService");
        dispatcherImpl.registerAppServer(ADDRESS_SERVER, PORT_SERVER, serverId, new DispatcherAppserverUpdaterImpl());

        //doe de initialisatie
        clients = new HashMap<>();
        gameUpdateSubscribers = new HashMap<>();
        try {
            Registry registry = LocateRegistry.createRegistry(PORT_SERVER);
            registry.rebind("ServerImplService", new ServerImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public static void main(String[] args) {
        try {
            serverId = Integer.parseInt(args[0]);
            ServerMain main = new ServerMain();
            main.startServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
