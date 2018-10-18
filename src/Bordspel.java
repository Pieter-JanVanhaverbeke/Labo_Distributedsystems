import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bordspel {
    private Kaart[][] bord;
    // private String type; //layout van Bordspel
    private int lengte;
    private int breedte;

    public Bordspel(int lengte, int breedte) {
        this.bord = new Kaart[lengte][breedte];
        this.lengte = lengte;
        this.breedte = breedte;

        for (int i = 0; i < lengte; i++) {
            for (int j = 0; j < breedte; j++) {
                bord[i][j] = new Kaart();
            }
        }

        //bij default alle kaarten al facedown
        initialiseerKaartType();          //iedere kaart van bordspel een soort geven

    }

    public Bordspel(Kaart[][] bord, int lengte, int breedte) {
        this.bord = bord;
        this.lengte = lengte;
        this.breedte = breedte;
    }

    public Kaart[][] getBord() {
        return bord;
    }

    public void setBord(Kaart[][] bord) {
        this.bord = bord;
    }

    public int getLengte() {
        return lengte;
    }

    public void setLengte(int lengte) {
        this.lengte = lengte;
    }

    public int getBreedte() {
        return breedte;
    }

    public void setBreedte(int breedte) {
        this.breedte = breedte;
    }

    public boolean checkEindeSpel() {
        for (int i = 0; i < lengte; i++) {
            for (int j = 0; j < breedte; j++) {
                if (!bord[i][j].isFaceUp()) return false;       //als 1 kaart facedown is, spel nog niet gedaan
            }
        }
        return true;
    }

    public void initialiseerKaartType() {
        List<Integer> lijst = new ArrayList<>(); //lijst met de verschillende soorten die verdeeld moeten worden onder kaarten


        for (int i = 0; i < lengte/2; i++) {
            for (int j = 0; j < 8; j++) {
                lijst.add(j);               // Spel bevat steeds 8 soorten, KAN AANPASSEN ALS NODIG
            }
        }



        //Iedere kaart random soort geven van de lijst
        Collections.shuffle(lijst);                         //lijst shufflen
        for(int i=0; i<lengte;i++){
            for(int j=0; j<breedte; j++){
                bord[i][j].setSoort(lijst.get(4*i+j));      //geef iedere kaart een soort
            }
        }

        //Iedere kaart random soort geven van de lijst
      /*  for (int i = 0; i < lengte; i++) {
            for (int j = 0; j <breedte; j++) {
                Random rand = new Random();
                int randomElement = lijst.get(rand.nextInt(lijst.size()));
                bord[i][j].setSoort(randomElement);
                System.out.println("random: " + randomElement +" size: "+ lijst.size());
                lijst.remove(randomElement);                                //gebruikte soort verwijderen
            }
        }*/
    }


    public void printBordspel() {
        for (int i = 0; i < lengte; i++) {
            System.out.println();
            for (int j = 0; j < breedte; j++) {
                bord[i][j].printKaart();
            }
        }
        System.out.println();
    }

    public void printBordspelFaceUp(){
        for (int i = 0; i < lengte; i++) {
            System.out.println();
            for (int j = 0; j < breedte; j++) {
                bord[i][j].printKaartFaceUp();
            }
        }
        System.out.println();
    }

}