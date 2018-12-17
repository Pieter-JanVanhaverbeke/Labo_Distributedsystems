package dispatcher;

import exceptions.NoServerAvailableException;
import shared_dispatcher_appserver_client_stuff.rmi_int_dispatcher_appserver_client;
import shared_dispatcher_client_stuff.RegisterClientRespons;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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

/**
 * Bevat alle methode die de client, de appserver en de databankserver kunnen oproepen op de dispatcher.
 */
public class DispatcherImpl extends UnicastRemoteObject implements rmi_int_dispatcher_appserver_client, Serializable {

    //db id is key
    private Map<Integer, DbInfo> dbInfoList = new HashMap<>();
    //serverId is key
    private Map<Integer, ServerInfo> serverlijst;
    private static int serverTeller = 0; // = id van server
    private static int dbTeller = 0; // = id van db
    private static int clientTeller = 0; // = id van client
    private boolean reallocating = false;

    public DispatcherImpl() throws RemoteException {
        serverlijst = new HashMap<>();
    }

    /////////////////////////////////// appservers ///////////////////////////////////

    //appserver had al port gekregen bij het opstarten van de jar

    /**
     * Registreer appserver bij dispatcher
     * @param ipAddress
     * @param port
     * @return de serverid
     */
    public synchronized int registerAppServer(String ipAddress, int port){
        serverlijst.put(serverTeller, new ServerInfo(ipAddress, port, serverTeller));
        System.out.println("registered AppServerId: " +  serverTeller);
        return serverTeller++;
    }

    /**
     * Vraag een dispacher of mag heralloceren. Dit voorkomt dat meerdere appservers tegelijk zouden
     * herverdelen en eventueel aflsuiten.
     * @param serverId
     * @return true als mag heralloceren, false anders.
     */
    //slechts 1 server per keer sluiten => permission vragen aan server
    public synchronized boolean reallocationRequest(int serverId){
        if(reallocating)
            return false;
        else{
            reallocating = true;
            System.out.println("Realocation true");
            return true;
        }
    }

    /**
     * Unregister appserver bij de dispatcher als gaat afsluiten.
     * @param serverId
     */
    //verwijder server uit lijst
    public synchronized void deleteAppServer(int serverId){
        reallocating = false;
        serverlijst.remove(serverId);
        System.out.println("ServerId: " + serverId + " deleted");
        System.out.println("reallocation false");
    }

    /**
     * Laat aan de dispatcher weten hoeveel active games er op de appserver staan.
     * @param serverId
     * @param usersCount
     */
    public synchronized void updateNumberOfGames(int serverId, int usersCount){
        ServerInfo serverInfo = serverlijst.get(serverId);
        if(serverInfo != null)
            serverInfo.setGameCount(usersCount);
        reallocating = false;
        System.out.println("reallocation false");

        serverlijst.forEach((k, v) -> System.out.println("ServerId: " + k + " -> gameId: " + v.getId() + ", game count: " + v.getGameCount()));
    }

    /**
     * Vraag aan dispatcher om nieuwe gameserver te starten.
     */
    @Override
    public synchronized void requestNewAppServer() {
        startAppServer();
    }

    /**
     * Registreer client bij opstart van client
     * @return Client krijgt een appserver toegekent
     * @throws NoServerAvailableException
     */
    //client krijgt clientId en appServer toegewezen
    public synchronized RegisterClientRespons registerClient() throws NoServerAvailableException {
        //return random appserver
        //prutsen
        return new RegisterClientRespons(getRandomServer(), clientTeller++);
    }

    /**
     * Raporteer een slechte appserver.
     * @param serverId
     * @return Een nieuwe appserver
     * @throws NoServerAvailableException
     */
    //verwijder server if nog niet eerder gebeurt + probeer server te down shutten
    //return een nieuwe appserver
    @Override
    public synchronized ServerInfo reportBadAppServer(int serverId) throws NoServerAvailableException {
        ServerInfo serverInfo = serverlijst.get(serverId);

        System.out.println("Bad app server: " + serverId);
        if (serverInfo != null){
            //check of server idd niet antwoord
            //if(pingServer(serverInfo.getIpAddress())){
            //    return serverInfo;
            //}
            serverlijst.remove(serverId);
            //serverInfo.getUpdater().shutDown();
        }

        return getRandomServer();

    }

    /**
     * Registreer een nieuwe databank server bij opstart van de databankserver
     * @param address
     * @param port
     * @return de databank id
     */
    public synchronized int registerDBServer(String address, int port){
        dbInfoList.put(dbTeller, new DbInfo(address, port, dbTeller));
        System.out.println("registered DBserverId: " +  dbTeller);
        return dbTeller++;
    }

    /**
     * Raporteer een slechte databankserver
     * @param dbId
     * @return een nieuwe databank server
     * @throws NoServerAvailableException
     */
    public synchronized DbInfo reportBadDbServer(int dbId) throws NoServerAvailableException {
        //verwijder oude uit lijst en return een nieuwe db server

        System.out.println("bad db: " + dbId);
        DbInfo dbInfo = dbInfoList.get(dbId);

        if(dbInfo != null)
            dbInfoList.remove(dbId);

        return getRandomDb();
    }

    /**
     * Return een lijst van alle active applicatieservers
     * @return
     */
    @Override
    public synchronized List<ServerInfo> getActiveAppServers() {
        return new ArrayList<>(serverlijst.values());
    }

    //start nieuwe server in nieuw process
    //stap 2 is dat de nieuwe server zichzelf registreerd bij de dispatcher via de rmi interface
    // daaruit kan de dispatcher het ip adress en het portnumber halen
    private synchronized void startAppServer(){
        try {
            System.out.println("Nieuwe server starten.");
            //get random db
            DbInfo dbInfo = getRandomDb();
            // Run a java app in a separate system process met als commamd line argument de id die de server krijgt
            Process proc = Runtime.getRuntime().exec("java -jar appserver.jar " + dbInfo.getAddress() + Integer.toString(dbInfo.getPort()));
            // Then retreive the process output
            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean pingServer(String ipadres) {
        try {
            //machine aanstaan != applicatie runnen
            /*InetAddress address = InetAddress.getByName(ipadres);
            boolean reachable = address.isReachable(10000);
            System.out.println("Is host reachable? " + reachable);
            return reachable;*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    private synchronized ServerInfo getRandomServer() throws NoServerAvailableException {
        if(serverlijst.isEmpty())
            throw new NoServerAvailableException("Geen applicatieServers beschikbaar.");
        return new ArrayList<>(serverlijst.values()).get((int)(Math.random()*serverlijst.size()));
    }

    private synchronized DbInfo getRandomDb() throws NoServerAvailableException {
        if(dbInfoList.isEmpty())
            throw new NoServerAvailableException("Geen dbServers beschikbaar.");
        return new ArrayList<>(dbInfoList.values()).get((int)(Math.random()*dbInfoList.size()));

    }


}
