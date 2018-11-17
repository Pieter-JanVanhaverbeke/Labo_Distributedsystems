package application_server;

import shared_client_appserver_stuff.rmi_int_client_appserver_updater;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMain {
    //clients waarnaar bord/lobby updates moet sturen
    public static Map<Integer, rmi_int_client_appserver_updater> clients;


    private void startServer() {
        clients = new HashMap<>();
        try {
            Registry registry = LocateRegistry.createRegistry(10001);
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
