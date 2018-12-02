package shared_dispatcher_client_stuff;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by ruben on 2/12/18.
 */
public interface rmi_int_dispatcher_client_updater extends Remote {

    void setNewAppserver(ServerInfo newServer) throws RemoteException;
}
