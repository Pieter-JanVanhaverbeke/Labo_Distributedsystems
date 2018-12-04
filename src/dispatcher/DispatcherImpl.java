package dispatcher;

import shared_dispatcher_appserver_client_stuff.rmi_int_dispatcher_appserver_client;
import shared_dispatcher_client_stuff.RegisterClientRespons;
import shared_dispatcher_client_stuff.ServerInfo;
import shared_dispatcher_client_stuff.rmi_int_dispatcher_client_updater;
import shared_dispatcher_appserver_stuff.memory_spel.Game;
import shared_dispatcher_appserver_stuff.rmi_int_dispatcher_appserver_updater;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruben on 2/12/18 in Ertvelde, Belgium. It's 14°C and cloudy. I am wearing a black sweater.
 * The people outside have no idea of what's happing inside this house.
 * They don't know that in this very spot, I am writing world changing software.
 * Software that could change the world we all live in.
 * Do you think the world is ready for this?
 * Should I keep this project a secret?
 * The world doesn't know me yet,
 * but I can change the way people think about memory...
 *
 * Mind my words...
 */
public class DispatcherImpl extends UnicastRemoteObject implements rmi_int_dispatcher_appserver_client, Serializable {

    private List<ServerInfo> serverlijst;
    private List<ClientInfo> clientLijst;
    private Map<String, ServerInfo> clientServerMapping;
    private static int serverTeller = 0; // = id van server?
    private static int clientTeller = 0; // = id van client?

    public DispatcherImpl() throws RemoteException {
        serverlijst = new ArrayList<>();
        clientLijst = new ArrayList<>();
        clientServerMapping = new HashMap<>();
    }

    //appserver had al port gekregen bij het opstarten van de jar
    public void registerAppServer(String ipAddress, int port, int id, rmi_int_dispatcher_appserver_updater updater){
        serverlijst.add(new ServerInfo(ipAddress, port, id, updater));
    }

    //client krijgt clientId en appServer toegewezen
    public RegisterClientRespons registerClient(String ipAddress, rmi_int_dispatcher_client_updater updater){
        clientLijst.add(new ClientInfo(ipAddress, clientTeller, updater));

        //zoek naar appserver, start eventueel een nieuwe op
        return new RegisterClientRespons(null, clientTeller++);
    }

    @Override
    public ServerInfo reportBadAppServer(ServerInfo badServer) {
        //verwijder server if nog niet eerder gebeurt + probeer server te down shutten
        //return een nieuwe appserver
        return null;
    }

    //start nieuwe server in nieuw process
    //stap 2 is dat de nieuwe server zichzelf registreerd bij de dispatcher via de rmi interface
    // daaruit kan de dispatcher het ip adress en het portnumber halen
    private void startAppServer(){
        try {
            // Run a java app in a separate system process met als commamd line argument de id die de server krijgt
            Process proc = Runtime.getRuntime().exec("java -jar appserver.jar " + serverTeller++);
            // Then retreive the process output
            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pingServer(String ipadres) {
        try {
            InetAddress address = InetAddress.getByName(ipadres);
            boolean reachable = address.isReachable(10000);

            System.out.println("Is host reachable? " + reachable);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //appserver laat weten hoeveel games er gestart zijn
    public void informDispatcher(int aantalgames){
        //get alle active games van elke app server
        Map<ServerInfo, Integer> aantalGames = new HashMap<>();
        for(ServerInfo serverInfo: serverlijst){
            aantalGames.put(serverInfo, serverInfo.getUpdater().getAantalgames());
        }

        //kijk of geen herallocatie kan doen tussen app servers
        List<ReAllocationUpdates> updates = reAllocateGames(aantalGames);

        for(ReAllocationUpdates update: updates){
            //voer updates uit
            //zender van Game object (getFrom) update port van clients nqqr -1 => client weet dat game
            //aan het verplaatsen => wacht totdat address aangepast is door ontvanger (getTo)
            //ontvanger van Game object (getTo) update de clients naar nieuw adres
            Game game = update.getFrom().getUpdater().getGameForReAllocation(update.getGameId());
            update.getTo().getUpdater().setGameForReAllocation(game);
        }

    }

    private List<ReAllocationUpdates> reAllocateGames(Map<ServerInfo, Integer> aantalGames){
        //check of kan heralloceren
        return null;
    }


}
