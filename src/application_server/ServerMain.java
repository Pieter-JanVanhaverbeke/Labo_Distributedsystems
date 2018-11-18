package application_server;

import shared_client_appserver_stuff.rmi_int_client_appserver_updater;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMain {
    public static final int PORTSERVER = 10001;

    //clients waarnaar bord/lobby updates moet sturen
    //key is username
    public static Map<String, rmi_int_client_appserver_updater> clients;
    //lijst van usernames per gameId, users die update moeten krijgen van game
    public static Map<Integer, List<String>> gameUpdateSubscribers;


    private void startServer() {
        clients = new HashMap<>();
        gameUpdateSubscribers = new HashMap<>();
        try {
            Registry registry = LocateRegistry.createRegistry(PORTSERVER);
            registry.rebind("ServerImplService", new ServerImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public static void main(String[] args) {
        ServerMain main = new ServerMain();
        main.startServer();
    }
}
