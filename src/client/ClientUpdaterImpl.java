package client;

import javafx.application.Platform;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.rmi_int_client_appserver_updater;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static client.ClientMainGUI.gameController;
import static client.ClientMainGUI.lobbyController;


/**
 * Created by ruben on 17/11/18.
 */
public class ClientUpdaterImpl extends UnicastRemoteObject implements rmi_int_client_appserver_updater, Serializable {

    public ClientUpdaterImpl() throws RemoteException{

    }

    @Override
    public void updateBord(GameInfo gameInfo) {
        Platform.runLater(() -> {
            if(gameController != null)
                gameController.updateBord(gameInfo);
        });
    }

    public void updateLobby(List<GameInfo> activeGames){
        Platform.runLater(() -> {
            if(lobbyController != null)
                lobbyController.updateLobby(activeGames);
        });
    }

    @Override
    public void updateGameAddress(String ipAddress, int port) {
        //gameSpecifiek ip/port aanpassen
    }
}
