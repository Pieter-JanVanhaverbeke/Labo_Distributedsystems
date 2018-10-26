package memory_spel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Bordspel {
    private Kaart[][] bord;
    private BordThema type; //layout van bordspel
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
                if (!bord[i][j].isFaceUp())
                    return false;  //als 1 kaart facedown is, spel nog niet gedaan
            }
        }
        return true;
    }

    // Spel bevat steeds 8 soorten, KAN AANPASSEN ALS NODIG
    public void initialiseerKaartType() {
        List<Integer> lijst = new ArrayList<>(); //lijst met de verschillende soorten die verdeeld moeten worden onder kaarten
        int aantal = 0;     //aantal keer//iedere speler start met 0 punten

        if (lengte == 4) {
            aantal = 2;
        } else if (lengte == 6) {
            aantal = 4;
        } else if (lengte == 8) {
            aantal = 8;
        }

        for (int i = 0; i < aantal; i++) {
            for (int j = 0; j < 8; j++) {
                lijst.add(j);               // Spel bevat steeds 8 soorten, KAN AANPASSEN ALS NODIG
            }
        }

        if (lengte == 6) {
            for (int i = 0; i < 2; i++) {
                Random rand = new Random();
                int randomnummer = rand.nextInt(7) + 1;
                lijst.add(randomnummer);
                lijst.add(randomnummer);
            }
        }


        //Iedere kaart random soort geven van de lijst
        Collections.shuffle(lijst);                         //lijst shufflen
        for (int i = 0; i < lengte; i++) {
            for (int j = 0; j < breedte; j++) {
                bord[i][j].setSoort(lijst.get(lengte * i + j));      //geef iedere kaart een soort
            }
        }

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