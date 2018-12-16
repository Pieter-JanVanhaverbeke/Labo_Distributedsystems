package shared_client_appserver_stuff;

import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ruben on 17/11/18.
 */
public interface rmi_int_client_appserver_updater extends Remote {


    void updateBord(GameInfo gameUpdate) throws RemoteException;

    void updateLobby(List<GameInfo> activeGames) throws RemoteException;

    void updateAppServer(ServerInfo serverInfo) throws RemoteException;
}
