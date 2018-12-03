package dispatcher;

import shared_dispatcher_client_stuff.rmi_int_dispatcher_client_updater;

import java.io.Serializable;

/**
 * Created by ruben on 2/12/18.
 */
public class ClientInfo implements Serializable {

    private String ipAddress;
    private int id;
    private rmi_int_dispatcher_client_updater updater;

    public ClientInfo(String ipAddress, int id, rmi_int_dispatcher_client_updater updater) {
        this.ipAddress = ipAddress;
        this.id = id;
        this.updater = updater;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public rmi_int_dispatcher_client_updater getUpdater() {
        return updater;
    }

    public void setUpdater(rmi_int_dispatcher_client_updater updater) {
        this.updater = updater;
    }
}
