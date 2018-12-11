package db_server.DbConnection.Chorde;

import java.util.HashMap;

public class Node {
 /*     *internal.Node
    predecessor *internal.Node
    successor *internal.Node
    fingerTable fingerTable
    storage Storage
    transport Transport
*/
 private int id;
 private Node predecessor;
 private Node successor;
 private HashMap<Integer,Node> fingertable;
 private int m;

    public Node() {
        predecessor = null;
        successor = null;
        id = 0;
        m = 3;
    }



    public Node find_successor(int id){

        if(isElementhalfclosed(id)){
            return successor;
        }
        else{            //zoek dichtste successor
            Node n0 = this.closest_preceding_node(id);
            return n0.find_successor(id);
        }
    }



    public Node closest_preceding_node(int id){
        for (int i=m; i>=1; i--){
            if(isElement(fingertable.get(i).id)){                    //     if (finger[i]∈(n,id))
                return fingertable.get(i);
            }
        }
        return this;
    }



    public void create(){
        predecessor = null;
        successor = this;
    }

    public void stabilize(){
        Node x = successor.predecessor;
        if(x.isElement(this,successor)){
            successor = x;
        }
        successor.notify(this);
    }

    public void notify(Node node){
        if(predecessor == null || node.isElement(predecessor,this) ){
            predecessor = node;
        }
    }


    public void fix_fingers(){


        int next = 0;
        next = (next+1)%m;

        Node node = find_successor((int)(this.id + Math.pow(2,next-1))); // finger[next] = find_successor(n+ {\displaystyle 2^{next-1}} 2^{next-1});
        fingertable.put(next,node);

    }


     public void check_predecessor(){
        if(predecessor == null){            //TODO ALS ERROR IS
            predecessor = null;
        }
     }




       /*  n.stabilize()
    x = successor.predecessor;
   if (x∈(n, successor))
    successor = x;
   successor.notify(n);
*/


// if (predecessor is nil or n'∈(predecessor, n))
 //   predecessor = n';


    public void join(Node node){
        predecessor = null;
        successor = node.find_successor(node.getId());  //TODO METHODE SCHRIJVEN OM SUCCESSOR TE VINDEN
    }






    private boolean isElement(int id){
        return id > this.id && id < this.successor.id;     //in interval (id,successor]
    }

    private boolean isElementhalfclosed(int id){
        return id > this.id && id <= this.successor.id;     //in interval (id,successor]
    }

    private boolean isElement(Node node){
        if(this.id>node.id && this.id<this.successor.id){
            return true;
        }
        else return false;
    }

    private boolean isElement(Node node, Node successor){
        if(this.id>node.getId() && id<successor.getId()){
            return true;
        }
        return false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Node getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    public Node getSuccessor() {
        return successor;
    }

    public void setSuccessor(Node successor) {
        this.successor = successor;
    }
}
