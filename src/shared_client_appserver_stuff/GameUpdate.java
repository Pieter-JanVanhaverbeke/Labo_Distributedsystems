package shared_client_appserver_stuff;

/**
 * Created by ruben on 3/11/18.
 */
public class GameUpdate {

    private int spelersBeurt;
    int[][] bord;

    public GameUpdate(int spelersBeurt, int[][] bord) {
        this.spelersBeurt = spelersBeurt;
        this.bord = bord;
    }

    public int getSpelersBeurt() {
        return spelersBeurt;
    }

    public void setSpelersBeurt(int spelersBeurt) {
        this.spelersBeurt = spelersBeurt;
    }

    public int[][] getBord() {
        return bord;
    }

    public void setBord(int[][] bord) {
        this.bord = bord;
    }
}
