package shared_dispatcher_client_stuff;

import shared_dispatcher_appserver_stuff.rmi_int_dispatcher_appserver_updater;

import java.io.Serializable;

/**
 * Created by ruben on 2/12/18.
 */
public class ServerInfo implements Serializable {

    private String ipAddress;
    private int portNumber;
    private int id;
    private rmi_int_dispatcher_appserver_updater updater;

    public ServerInfo(String ipAddress, int portNumber, int id, rmi_int_dispatcher_appserver_updater updater) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.id = id;
        this.updater = updater;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public rmi_int_dispatcher_appserver_updater getUpdater() {
        return updater;
    }

    public void setUpdater(rmi_int_dispatcher_appserver_updater updater) {
        this.updater = updater;
    }
}
