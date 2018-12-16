package dispatcher;

import java.io.Serializable;

/**
 * Created by ruben on 8/12/18.
 */
public class DbInfo implements Serializable {
    private String address;
    private int port;
    private int dbId;

    public DbInfo(String address, int port, int id) {
        this.address = address;
        this.port = port;
        this.dbId = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
