package shared_dispatcher_appserver_client_stuff;

import dispatcher.DbInfo;
import exceptions.NoServerAvailableException;
import shared_dispatcher_client_stuff.RegisterClientRespons;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ruben on 2/12/18.
 */
public interface rmi_int_dispatcher_appserver_client extends Remote {

    int registerAppServer(String ipAddress, int port) throws RemoteException;

    RegisterClientRespons registerClient() throws RemoteException, NoServerAvailableException;

    ServerInfo reportBadAppServer(int serverId) throws RemoteException, NoServerAvailableException;

    DbInfo reportBadDbServer(int dbId) throws RemoteException, NoServerAvailableException;

    List<ServerInfo> getActiveAppServers() throws RemoteException;

    boolean reallocationRequest(int serverId) throws RemoteException;

    void deleteAppServer(int serverId) throws RemoteException;

    void updateNumberOfGames(int serverId, int usersCount) throws RemoteException;

    void requestNewAppServer() throws RemoteException;

    int registerDBServer(String addressDb, int portDb) throws RemoteException;
}
