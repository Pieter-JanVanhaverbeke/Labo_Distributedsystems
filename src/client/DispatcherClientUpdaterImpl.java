package client;

import shared_dispatcher_client_stuff.ServerInfo;
import shared_dispatcher_client_stuff.rmi_int_dispatcher_client_updater;

import java.io.Serializable;
import java.rmi.RemoteException;

import static client.ClientMainGUI.setAddressServer;
import static client.ClientMainGUI.setPortServer;

/**
 * Created by ruben on 2/12/18.
 */
public class DispatcherClientUpdaterImpl implements rmi_int_dispatcher_client_updater, Serializable {

    @Override
    public void setNewAppserver(ServerInfo newServer) throws RemoteException {
        setAddressServer(newServer.getIpAddress());
        setPortServer(newServer.getPortNumber());
    }

}
