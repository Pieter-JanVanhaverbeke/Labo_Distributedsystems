package shared_dispatcher_appserver_stuff;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by ruben on 9/12/18.
 */
public interface rmi_int_dispatcher_appserver extends Remote {

    int ping() throws RemoteException;
}
