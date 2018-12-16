package shared_dispatcher_appserver_stuff;

import java.rmi.RemoteException;

/**
 * Created by ruben on 11/12/18.
 */
public interface rmi_int_dispatcher_appserver_updater {
    int getActiveGamesCount() throws RemoteException;
}
