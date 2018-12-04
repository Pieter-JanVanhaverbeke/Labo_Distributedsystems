package application_server;

import shared_dispatcher_appserver_stuff.memory_spel.Game;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.rmi_int_client_appserver_updater;

import java.rmi.RemoteException;

import static application_server.ServerMain.ADDRESS_SERVER;
import static application_server.ServerMain.PORT_SERVER;
import static application_server.ServerMain.clients;

/**
 * Created by ruben on 17/11/18.
 */
public class GameUpdateTask implements Runnable {
    private Game game;

    public GameUpdateTask(Game game){
        this.game = game;
    }

    @Override
    public void run() {
        for(rmi_int_client_appserver_updater updater: clients.values()) {
            try {
                updater.updateBord(new GameInfo(game, ADDRESS_SERVER, PORT_SERVER));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
}
