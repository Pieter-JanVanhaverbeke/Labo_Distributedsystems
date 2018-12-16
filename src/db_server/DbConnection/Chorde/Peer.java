package db_server.DbConnection.Chorde;

import java.io.Serializable;

public class Peer implements Serializable {
    private int predecessor;        //TODO IP GEVEN IPV PREDECESSOR?
    private int successor;
    private int id;

    private int successorip;

    public Peer(int predecessor, int successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    public Peer() {
    }

    public int getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(int predecessor) {
        this.predecessor = predecessor;
    }

    public int getSuccessor() {
        return successor;
    }

    public void setSuccessor(int successor) {
        this.successor = successor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSuccessorip() {
        return successorip;
    }

    public void setSuccessorip(int successorip) {
        this.successorip = successorip;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id=" + id +
                ", predecessor=" + predecessor +
                ", successor=" + successor +
                '}';
    }
}
