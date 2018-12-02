package shared_dispatcher_client_stuff;

/**
 * Created by ruben on 2/12/18.
 */
public class RegisterClientRespons {
    private ServerInfo serverInfo;
    private int id;

    public RegisterClientRespons(ServerInfo serverInfo, int id) {
        this.serverInfo = serverInfo;
        this.id = id;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
