package shared_dispatcher_appserver_stuff.memory_spel;

import java.io.Serializable;

public class Kaart implements Serializable {
    private int soort; //kaarttype
    private boolean faceUp;

    public Kaart(int soort, boolean faceUp) {
        this.soort = soort;
        this.faceUp = faceUp;
    }

    public Kaart() {
        this.soort = 1;
        this.faceUp = false;
    }

    public int getSoort() {
        return soort;
    }

    public void setSoort(int soort) {
        this.soort = soort;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public void draaiOm(){
        faceUp = !faceUp; //flippen van de kaart
    }

    public void printKaart(){
        if (!faceUp){
            System.out.print("| . |");
        }
        else{
            System.out.print("| " + soort + " |");
        }
    }


    public void printKaartFaceUp(){
        System.out.print("| " + soort + " |");
    }



}
