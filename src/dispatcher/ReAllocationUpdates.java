package dispatcher;

import shared_dispatcher_client_stuff.ServerInfo;

/**
 * Created by ruben on 4/12/18.
 */

//deze klasse geeft aan welke games vanuit welke
// appserver naar welke andere appserver moet geplaats worden
public class ReAllocationUpdates {
    private ServerInfo from;
    private ServerInfo to;
    private int gameId;

    public ReAllocationUpdates(ServerInfo from, ServerInfo to, int gameId) {
        this.from = from;
        this.to = to;
        this.gameId = gameId;
    }

    public ServerInfo getFrom() {
        return from;
    }

    public void setFrom(ServerInfo from) {
        this.from = from;
    }

    public ServerInfo getTo() {
        return to;
    }

    public void setTo(ServerInfo to) {
        this.to = to;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
