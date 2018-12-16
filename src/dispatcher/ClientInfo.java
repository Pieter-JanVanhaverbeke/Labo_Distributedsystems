package dispatcher;


import java.io.Serializable;

/**
 * Created by ruben on 2/12/18.
 */
public class ClientInfo implements Serializable {

    private String ipAddress;
    private int id;

    public ClientInfo(String ipAddress, int id) {
        this.ipAddress = ipAddress;
        this.id = id;
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

}
