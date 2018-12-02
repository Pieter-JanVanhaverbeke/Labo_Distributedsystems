package shared_dispatcher_appserver_client_stuff;

import shared_dispatcher_client_stuff.RegisterClientRespons;
import shared_dispatcher_client_stuff.ServerInfo;
import shared_dispatcher_client_stuff.rmi_int_dispatcher_client_updater;
import shated_dispatcher_appserver_stuff.rmi_int_dispatcher_appserver_updater;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by ruben on 2/12/18.
 */
public interface rmi_int_dispatcher_appserver_client extends Remote {

    void registerAppServer(String ipAddress, int port, int id, rmi_int_dispatcher_appserver_updater updater) throws RemoteException;

    RegisterClientRespons registerClient(String ipAddress, rmi_int_dispatcher_client_updater updater) throws RemoteException;

    ServerInfo reportBadAppServer(ServerInfo badServer) throws RemoteException;

}
