package shared_dispatcher_appserver_stuff;

import shared_dispatcher_appserver_stuff.memory_spel.Game;

import java.rmi.Remote;

/**
 * Created by ruben on 2/12/18.
 */
public interface rmi_int_dispatcher_appserver_updater extends Remote {

    void shutDownAppserver();

    int getAantalgames();

    Game getGameForReAllocation(int gameId);

    void setGameForReAllocation(Game game);
}
