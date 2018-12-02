package shated_dispatcher_appserver_stuff;

import java.rmi.Remote;

/**
 * Created by ruben on 2/12/18.
 */
public interface rmi_int_dispatcher_appserver_updater extends Remote {

    void shutDownAppserver();
}
