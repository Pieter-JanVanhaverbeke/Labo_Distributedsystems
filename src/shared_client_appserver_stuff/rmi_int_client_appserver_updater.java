package shared_client_appserver_stuff;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ruben on 17/11/18.
 */
public interface rmi_int_client_appserver_updater extends Remote {


    void updateBord(GameInfo gameUpdate) throws RemoteException;

    void updateLobby(List<GameInfo> activeGames) throws RemoteException;

    void updateGameAddress(String ipAddress, int port);
}
