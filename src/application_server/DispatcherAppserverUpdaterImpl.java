package application_server;

import shared_dispatcher_appserver_stuff.memory_spel.Game;
import shared_dispatcher_appserver_stuff.memory_spel.Lobby;
import shared_dispatcher_appserver_stuff.memory_spel.Speler;
import shared_dispatcher_appserver_stuff.rmi_int_dispatcher_appserver_updater;

import static application_server.ServerMain.ADDRESS_SERVER;
import static application_server.ServerMain.PORT_SERVER;
import static application_server.ServerMain.clients;

/**
 * Created by ruben on 2/12/18.
 */
public class DispatcherAppserverUpdaterImpl implements rmi_int_dispatcher_appserver_updater {

    @Override
    public void shutDownAppserver() {
        //update databanken
        //en andere dingen enzo
    }

    @Override
    public int getAantalgames() {
        return 0;
    }

    @Override
    public Game getGameForReAllocation(int gameId) {
        //wrm geen GameUpdate sturen??
        Game game = Lobby.getGame(gameId);
        for(Speler speler: game.getSpelers()){
            clients.get(speler.getUsername()).updateGameAddress(null, -1);
        }

        return game;
    }

    @Override
    public void setGameForReAllocation(Game game) {

        for(Speler speler: game.getSpelers()){
            clients.get(speler.getUsername()).updateGameAddress(ADDRESS_SERVER, PORT_SERVER);
        }
    }
}
