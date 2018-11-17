package client;

import shared_client_appserver_stuff.rmi_int_client_appserver_updater;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by ruben on 17/11/18.
 */
public class ClientUpdaterImpl extends UnicastRemoteObject implements rmi_int_client_appserver_updater, Serializable {

    public ClientUpdaterImpl() throws RemoteException{


    }
}
