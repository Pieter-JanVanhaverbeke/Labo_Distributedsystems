package client;

import exceptions.NoServerAvailableException;
import exceptions.NoValidTokenException;
import javafx.application.Platform;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.rmi_int_client_appserver_updater;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static client.ClientMainGUI.*;


/**
 * Created by ruben on 17/11/18.
 * Bvat alle methodes die de appserver op de client kan oproepen.
 */
public class ClientUpdaterImpl extends UnicastRemoteObject implements rmi_int_client_appserver_updater, Serializable {

    public ClientUpdaterImpl() throws RemoteException{

    }

    /**
     * Verbind deze client met een andere applicatieserver.
     * @param serverInfo
     */
    public synchronized void updateAppServer(ServerInfo serverInfo){
        try {
            serverImpl.unregisterClient(token);
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
        connectToAppServer(serverInfo);
    }

    /**
     * Update het spel bord met de nieuwe bordinfo.
     * @param gameInfo
     */
    @Override
    public synchronized void updateBord(GameInfo gameInfo) {
        Platform.runLater(() -> {
            if(gameController != null)
                gameController.updateBord(gameInfo);
        });
    }

    /**
     * Update de lobby met de nieuwe gameinfo
     * @param activeGames
     */
    public synchronized void updateLobby(List<GameInfo> activeGames){
        Platform.runLater(() -> {
            if(lobbyController != null)
                lobbyController.updateLobby(activeGames);
        });
    }
}
