package shared_client_appserver_stuff;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by ruben on 17/11/18.
 */
public interface rmi_int_client_appserver_updater extends Remote {


    void updateBord(GameInfo gameUpdate) throws RemoteException;
}
