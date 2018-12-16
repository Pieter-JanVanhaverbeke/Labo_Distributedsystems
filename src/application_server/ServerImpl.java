package application_server;

import application_server.memory_spel.Game;
import application_server.memory_spel.Speler;
import exceptions.*;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.rmi_int_client_appserver;
import shared_client_appserver_stuff.rmi_int_client_appserver_updater;
import shared_db_appserver_stuff.rmi_int_appserver_db;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static application_server.ServerMain.*;
import static application_server.Utils.Constants.*;
import static application_server.Utils.Utils.generateUserToken;
import static application_server.Utils.Utils.validateToken;

public class ServerImpl extends UnicastRemoteObject implements rmi_int_client_appserver, Serializable {

    public ServerImpl() throws RemoteException {

        try {
            Registry registryServer = LocateRegistry.getRegistry(ADDRESSDB, PORTDB);
            dbImpl = (rmi_int_appserver_db) registryServer.lookup("DbServerImplService");
            System.out.println("DB connection ok");
            dbUpdate();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (ConnectException ce) {
            try {
                renewDbServer();
            } catch (NoServerAvailableException e) {
                e.printStackTrace();
            }
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        }
    }


    //////////////////////////////// Control //////////////////////////////////////////
    @Override
    public String registrerNewClient(String username, String passwdHash, String salt, rmi_int_client_appserver_updater clientUpdater) throws UsernameAlreadyInUseException, RemoteException, NoServerAvailableException {
        try {
            int clientId = dbImpl.createUser(username, passwdHash, salt);
            System.out.println("gebruiker: " + username + " aangemaakt en aangemeld!");
            clients.put(username, clientUpdater);
            return generateUserToken(username);
        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            return registrerNewClient(username, passwdHash, salt, clientUpdater);
        }
    }

    //user toevoegen aan deze server zonder te moeten aanmelden (wel valid token nodig)
    //voor als user herverdeeld worden bij het afsluiten/crashen van een server
    public void registerClient(String token, rmi_int_client_appserver_updater clientUpdater) throws NoValidTokenException, RemoteException, NoServerAvailableException {
        Speler speler = validateToken(token);
        clients.put(speler.getUsername(), clientUpdater);
        //check of game aanwezig is gebeurt in game methodes (pull van db als gameId niet aanwezig is)
    }

    @Override
    public String logIn(String username, String passwordHash, rmi_int_client_appserver_updater clientUpdater) throws WrongPasswordException, UserDoesNotExistException, RemoteException, NoServerAvailableException {
        try {
            Speler speler = dbImpl.getSpeler(username);

            if (speler == null) {
                throw new UserDoesNotExistException("De gebruiker met gebruikersnaam: " + username + " bestaat niet.");
            } else {
                if (passwordHash.equals(speler.getPasswordHash())) {
                    //client toevoegen voor updates naar te sturen
                    clients.put(username, clientUpdater);
                    spelers.put(username, speler);
                    return generateUserToken(username);
                } else {
                    throw new WrongPasswordException("Het wachtwoord is verkeert.");
                }
            }
        }
        catch (ConnectException re) {
            re.printStackTrace();
            renewDbServer();
            return logIn(username, passwordHash, clientUpdater);
        }
    }
    @Override
    public void logout(String clientUsername) {
        clients.remove(clientUsername);
        spelers.remove(clientUsername);
    }

    @Override
    public String getSalt(String username) throws UserDoesNotExistException, RemoteException, NoServerAvailableException {
        try {
            return dbImpl.getSalt(username);
        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            return getSalt(username);
        }
    }


    //////////////////////////////// Lobby //////////////////////////////////////////
    //returned de gameId van de gemaakte game
    @Override
    public String createGame(int aantalSpelers, int bordGrootte, String token, int style) throws GameNotCreatedException, NoValidTokenException, InternalException, NoServerAvailableException {
        String gameId;
        try {
            String creator = validateToken(token).getUsername();

            try {
                if (aantalSpelers <= MAX_PLAYER_COUNT && aantalSpelers >= MIN_PLAYER_COUNT && bordGrootte >= MIN_BOARD_SIZE && bordGrootte <= MAX_BOARD_SIZE) {
                    Game game = new Game(bordGrootte, aantalSpelers, creator, style);
                    game.setServerInfo(new ServerInfo(ADDRESS_SERVER, PORT_SERVER, serverId));

                    String type = game.getBordspel().zetBordspelTypeOmNaarString();
                    String faceup = game.getBordspel().zetBordspelOmNaarString();

                    //nieuwe game in db zetten
                    gameId = dbImpl.createGame(creator, game.getCreateDate(), false, game.getAantalspelers(), bordGrootte, style, type, faceup, ADDRESS_SERVER, PORT_SERVER, serverId);
                    game.setGameId(gameId);
                    activeGames.put(gameId, game);
                    dispatcherImpl.updateNumberOfGames(serverId, activeGames.size());
                }
                else
                    throw new GameNotCreatedException("aantal spelers/bordgrootte niet toegelaten.");
            }
            catch (ConnectException re){
                re.printStackTrace();
                renewDbServer();
                gameId = createGame(aantalSpelers, bordGrootte, creator, style);
            }

            updateLobbyClients();
            return gameId;

        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //voegt speler toe aan game spelerslijst
    //speler sws verbonden met game waarop gespeeld gaat worden, if niet hierop
    // staat => dit wordt gameserver
    @Override
    public void joinGame(String gameId, String token) throws NoValidTokenException, PlayerNumberExceededException, InternalException, NoServerAvailableException {
        try {
            Speler speler = validateToken(token);
            Game game = activeGames.get(gameId);
            if(game == null)
                game = getKnownGame(gameId);
            game.addSpeler(speler);
            updateLobbyClients();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //verwijderd speler van game spelerslijst als game nog niet gestart is
    //speler sws verbonden met game waarop gespeeld gaat worden, if niet hierop
    // staat => dit wordt gameserver
    @Override
    public void unJoinGame(String gameId, String token) throws NoValidTokenException, GameAlreadyStartedException, InternalException, NoServerAvailableException {
        try {
            Speler speler = validateToken(token);
            Game game = activeGames.get(gameId);
            if(game == null)
                game = getKnownGame(gameId);
            game.removeSpeler(speler);
            updateLobbyClients();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");

        }
    }

    //returned de gekende games op deze server
    @Override
    public List<GameInfo> getActiveGamesList(String token) throws NoValidTokenException, RemoteException, NoServerAvailableException {
        validateToken(token);
        return getActiveGames();
    }

    private List<GameInfo> getActiveGames() {
        List<GameInfo> result = new ArrayList<>();

        if (activeGames != null) {
            for (String key : activeGames.keySet()) {
                Game game = activeGames.get(key);
                result.add(new GameInfo(game));
            }
        }

        if(knownGames != null) {
            for (String key : knownGames.keySet()) {
                Game game = knownGames.get(key);
                result.add(new GameInfo(game));
            }
        }

        return result;
    }


    @Override
    public GameInfo getGameForLobby(String token, String gameId) throws NoValidTokenException, RemoteException, NoServerAvailableException {
        validateToken(token);

        if(activeGames.containsKey(gameId))
            return new GameInfo(activeGames.get(gameId));
        if(knownGames.containsKey(gameId))
            return new GameInfo(knownGames.get(gameId));

        //als game niet gekent is => update van db
        dbUpdate();

        if(knownGames.containsKey(gameId))
            return new GameInfo(knownGames.get(gameId));

        return null;
    }

    @Override
    public GameInfo getGameForPlaying(String token, String gameId, boolean reallocation) throws RemoteException, NoValidTokenException, InternalException, NoServerAvailableException {
        validateToken(token);

        Game game = null;

        if(activeGames.containsKey(gameId))
            game = activeGames.get(gameId);
        else if(knownGames.containsKey(gameId))
            game = knownGames.get(gameId);

        //als gane niet gekent is (kan wel mss op andere server staan) return null
        if(game == null)
            return null;

        //als game op geen server staat => speel op deze server
        if(game.getServerInfo() == null || (game.getServerInfo().getId() == serverId && !activeGames.containsKey(gameId)) || (reallocation && dispatcherImpl.reallocationRequest(serverId))){
            activeGames.put(gameId, game);
            knownGames.remove(gameId);
            game.setServerInfo(new ServerInfo(ADDRESS_SERVER, PORT_SERVER, serverId));
            dispatcherImpl.updateNumberOfGames(serverId, activeGames.size());
            System.out.println("GameId: " + game.getGameId() + " is op deze server (" + serverId + ") gezet");
            return new GameInfo(game);
        }

        return new GameInfo(game);
    }

    @Override
    public void registerWatcher(String token, String gameId) throws RemoteException, NoServerAvailableException {
        try {
            Speler speler = validateToken(token);
            if(clients.containsKey(speler.getUsername())){
                List<String> lijst = gameUpdateSubscribers.computeIfAbsent(gameId, k -> new ArrayList<>());
                lijst.add(speler.getUsername());
            }

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unRegisterWatcher(String token, String gameId) throws RemoteException, NoServerAvailableException {
        try {
            Speler speler = validateToken(token);
            List<String> lijst = gameUpdateSubscribers.get(gameId);
            if(lijst != null)
                lijst.remove(speler.getUsername());

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////// Game ///////////////////////////////////////////
    //speler sws verbonden met game waarop gespeeld gaat worden, if niet hierop
    // staat => dit wordt gameserver
    @Override
    public void flipCard(String token, String gameId, int x, int y) throws NoValidTokenException, NotYourTurnException, NotEnoughSpelersException, InternalException, NoServerAvailableException {
        try {
            Speler speler = validateToken(token);
            //omdat enkel na gevonden paar pas in db komt => activeGames niet updates vanuit db zolang 2e kaart niet gedraaid is
            Game game = getAndMakeActiveGameAvailable(gameId);

            if(!game.flipCard(x, y, speler)) { //flipcard moet local nog veranderen!
                //dbUpdateGames(); //local updaten, dan naar db updaten!! niet omgekeert
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            throw new InternalException("Fout in verbinding met DB.");
        }
    }

    //speler sws verbonden met game waarop gespeeld gaat worden, if niet hierop
    // staat => dit wordt gameserver
    @Override
    public void startGame(String gameId, String token) throws NoValidTokenException, RemoteException, NoServerAvailableException {
        try {
            validateToken(token);
            Game game = activeGames.get(gameId);
            if(game == null)
                game = getKnownGame(gameId);
            game.setStarted(true);
            dbImpl.setStarted(true, gameId);
            updateLobbyClients();
        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            startGame(gameId, token);
        }
    }


    @Override
    public void deleteGame(String gameId) throws RemoteException, NoServerAvailableException {
        try {
            activeGames.remove(gameId);
            dbImpl.deleteGame(gameId);
            updateLobbyClients();
        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            deleteGame(gameId);
        }

    }

    //elke server ook

    //mss beter lokale cache van alle gameInfos bijhouden en na t s updaten vanuit dbs
    private void updateLobbyClients() throws RemoteException {
        List<GameInfo> l = getActiveGames();
        for(rmi_int_client_appserver_updater updater: clients.values()){
            updater.updateLobby(l);
        }
    }

    private void dbUpdate() throws RemoteException, NoServerAvailableException {
        try {
            //complete update from db
            knownGames = new HashMap<String, Game>();
            dbImpl.getAllGames().forEach((k, v) -> {
                if(!activeGames.containsKey(k))
                    knownGames.put(k, v);
            });
            System.out.println("full known games update from db");

        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            dbUpdate();
        }
    }

    //////////////////////////////////// distributed ////////////////////////////

    //is game aanwezig op deze server ( => op appserver wordt de game gespeeld) => return game
    //else als vraag komt van client => appserver van game is down ofzo iets => haal
    // uit db en deze server wordt gameserver
    private Game getAndMakeActiveGameAvailable(String gameId) throws RemoteException {
        if(activeGames.containsKey(gameId))
            return activeGames.get(gameId);

        else{
            //haal game uit db
            Game game = dbImpl.getGame(gameId);
            if(game != null) {
                activeGames.put(gameId, game);
                knownGames.remove(gameId);
                game.setServerInfo(new ServerInfo(ADDRESS_SERVER, PORT_SERVER, serverId));
                return game;
            }
            else{
                //afhandeling
                return null;
            }
        }
    }

    //client doet actie op game met gameId, als server deze game niet
    // kent => known games updaten
    private Game getKnownGame(String gameId) throws RemoteException, NoServerAvailableException {
        if(knownGames.containsKey(gameId))
            return knownGames.get(gameId);

        else{
            dbUpdate();
            return knownGames.get(gameId);
        }

    }

    //check of kan heralloceren (als over upperbound zit
    public void checkUpperGamesCount() throws RemoteException {

        //vraag huidige gameconfig van alle servers
        List<ServerInfo> servers = dispatcherImpl.getActiveAppServers();
        //remove self
        servers.removeIf(e -> e.getId() == serverId);

        //als teveel games zijn: verdeel games totdat gelijk verdeeld is (ongeveer 15)
        if (activeGames.size() > MAX_GAME_NUMBER) {
            try {
                verdeel(MEAN_GAME_NUMBER, activeGames.size() - MEAN_GAME_NUMBER, servers);
            } catch (NoServerAvailableException e) {
                e.printStackTrace();
            }

            // nog altijd te veel => verhoog grens
            if (activeGames.size() > MAX_GAME_NUMBER) {
                try {
                    verdeel(MAX_GAME_NUMBER, activeGames.size() - MAX_GAME_NUMBER, servers);
                } catch (NoServerAvailableException e) {
                    e.printStackTrace();
                }
            }

            // nog altijd te veel en kon niet herverdelen => nieuwe server starten
            if (activeGames.size() > MAX_GAME_NUMBER) {
                //vraag aan dispatcher om nieuwe te starten
                dispatcherImpl.requestNewAppServer();
                dispatcherImpl.updateNumberOfGames(serverId, activeGames.size());
                checkUpperGamesCount();
            }

        }
    }

    //check of kan heralloceren (als onder lowebound zit)
    public void checkLowerGamesCount() throws RemoteException {

        //vraag huidige gameconfig van alle servers
        List<ServerInfo> servers = dispatcherImpl.getActiveAppServers();
        //remove self
        servers.removeIf(e -> e.getId() == serverId);

        if(servers.size() == 0)
            return;

        if(activeGames.size() < MIN_GAME_NUMBER){
             //kijk of ergens plaats is om rommel te zetten zodat deze kan down shutten
             int count = 0;
             for(ServerInfo serverInfo: servers)
                 count += (MAX_GAME_NUMBER - serverInfo.getGameCount());
             //if nergens plaats => doe niets (niet nuttig om nieuwe server
             // te starten om deze te down shutten -_-
             if (count >= activeGames.size()) {
                 //herverdeel, en shutdown
                 try {
                     verdeel(MAX_GAME_NUMBER, activeGames.size(), servers);

                     if(activeGames.size() == 0) {
                         dispatcherImpl.deleteAppServer(serverId);
                         shutDown();
                     }
                 } catch (NoServerAvailableException e) {
                     e.printStackTrace();
                 }
             }
        }
    }

    //par:aantal is grens van aatnal games op server, if minder op server => kan nog games bij
    private void verdeel(int bound, int aantal, List<ServerInfo> servers) throws NoServerAvailableException {
        //stamp alles naar db + update clients
        try {

            //vraag eerst of mag herstructureren (zodat niet allemaal tegelijk afsluit)
            if(!dispatcherImpl.reallocationRequest(serverId))
                return;


            for (ServerInfo serverInfo: servers) {
                int number = serverInfo.getGameCount();

                int t = bound - number; //hoeveel plaats is nog over
                if (t > 0) {
                    //zet t aantal games op db en notify gebruikers dat server veranderd is
                    for (int i = 0; i < t && i < aantal; i++) {
                        Game game = activeGames.values().iterator().next();
                        game.setServerInfo(serverInfo);
                        pushToDb(game);
                        List<String> players = gameUpdateSubscribers.get(game.getGameId());
                        if(players != null)
                            for (String e : players) {
                                clients.get(e).updateAppServer(serverInfo);
                            }
                        knownGames.put(game.getGameId(), activeGames.remove(game.getGameId()));
                        System.out.println("GameId: " + game.getGameId() + " verzet naar serverId: " + serverInfo.getId());
                    }
                }
            }

            dispatcherImpl.updateNumberOfGames(serverId, activeGames.size());
        }
        catch (RemoteException r){
            r.printStackTrace();
        }
        catch (NoSuchElementException snee){
            snee.printStackTrace();
        }
    }

    private void pushToDb(Game game) throws RemoteException, NoServerAvailableException {
        //alles dat gecached is updaten naar db
        try {
            dbImpl.fullUpdate(game);
            System.out.println("GameId: " + game.getGameId() + " pushed to db");
        } catch (ConnectException e) {
            e.printStackTrace();
            renewDbServer();
            pushToDb(game);
        }
    }

    public static void shutDown(){
        System.out.println("shut down");
        System.exit(0);
    }



}
