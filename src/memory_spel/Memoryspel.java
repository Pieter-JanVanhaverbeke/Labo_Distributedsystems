package memory_spel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Memoryspel {
    private Bordspel bordspel;
    private boolean gedaan;
    private  int spelerbeurt;     //teller dat zegt wie aan beurt is
    private int aantalspelers;
    private List<Integer> puntenlijst; //was HashMap -> waarom?

    //   private memory_spel.Kaart [] [] bordspel;
    //   private List<memory_spel.Speler> spelerslijst;

                                                                    //SMALL = 4X4 MED = 6X6 LARGE = 8X8
                                                                    //Steeds 8 soorten
    public Memoryspel(int aantalspelers, int bordgrootte) {         //bordgrootte 1=small,2=medium,3=large

        int size = (int)Math.pow((2*bordgrootte+2),2);
        bordspel = new Bordspel(2*bordgrootte+2,2*bordgrootte+2);
        gedaan = false;
        spelerbeurt = 0;
        this.aantalspelers = aantalspelers;

        puntenlijst = new ArrayList<>();
        for(int i=0; i<aantalspelers;i++){                      //iedere speler start met 0 punten
            puntenlijst.add(0);
        }

    }

    public Memoryspel(Bordspel bordspel, boolean gedaan, int spelerbeurt, int aantalspelers) {
        this.bordspel = bordspel;
        this.gedaan = gedaan;
        this.spelerbeurt = spelerbeurt;
        this.aantalspelers = aantalspelers;

        puntenlijst = new ArrayList<>();
        for(int i=0; i<aantalspelers;i++){                      //iedere speler start met 0 punten
            puntenlijst.add(0);
        }

    }

    public Bordspel getBordspel() {
        return bordspel;
    }

    public void setBordspel(Bordspel bordspel) {
        this.bordspel = bordspel;
    }

    public boolean isGedaan() {
        return gedaan;
    }

    public void setGedaan(boolean gedaan) {
        this.gedaan = gedaan;
    }

    public int getSpelerbeurt() {
        return spelerbeurt;
    }

    public void setSpelerbeurt(int spelerbeurt) {
        this.spelerbeurt = spelerbeurt;
    }

    public int getAantalspelers() {
        return aantalspelers;
    }

    public void setAantalspelers(int aantalspelers) {
        this.aantalspelers = aantalspelers;
    }

    public List<Integer> getPuntenlijst() {
        return puntenlijst;
    }

    public void setPuntenlijst(List<Integer> puntenlijst) {
        this.puntenlijst = puntenlijst;
    }

    public void flipCard(int x, int y){         //x-coordinaat en y=coordiaat
        bordspel.getBord()[x][y].draaiOm();     //omdraaien specifieke kaart
    }

    public void controlleergedaan(){
       gedaan = bordspel.checkEindeSpel();
    }

    public boolean juisteBeurt(){
        return false;
    }

    public List<Integer> start(){
        Scanner sc = new Scanner(System.in);
        while(!gedaan){

            int lengte;
            int breedte;


            Kaart kaart1;
            Kaart kaart2;


            //spel overlopen



            //1) memory_spel.Speler aan de beurt kiest een kaart
            System.out.println();
            System.out.println("speler " + (spelerbeurt+1) + " is aan de beurt");
            bordspel.printBordspelFaceUp();

            lengte = sc.nextInt();
            breedte = sc.nextInt();

            //2) memory_spel.Kaart wordt omgedraait

            //NOG ZORGEN DAT FACEUPKAARTEN NIET OMGEDRAAIT KUNNEN WORDEN

            kaart1 = bordspel.getBord()[lengte][breedte];
            kaart1.draaiOm();

            bordspel.printBordspel();

            //3) 2de kaart wordt gekozen

            lengte = sc.nextInt();
            breedte = sc.nextInt();

            kaart2=bordspel.getBord()[lengte][breedte];
            kaart2.draaiOm();


            bordspel.printBordspel();

            //4) memory_spel.Kaart wordt omgedraait

            //NOG ZORGEN DAT FACEUPKAARTEN NIET OMGEDRAAIT KUNNEN WORDEN


            //5) Beiden gelijk, blijven facup, anders weer beide omgedraait
            if (kaart1.getSoort()!=(kaart2.getSoort())){
                System.out.println("verkeerd");
                kaart1.draaiOm();
                kaart2.draaiOm();

                //6) volgende beurt

            //volgende persoon bij verkeerd gok

                spelerbeurt++;
                spelerbeurt = spelerbeurt%aantalspelers;
            }

            else{
                System.out.println("juist");
                puntenlijst.set(spelerbeurt, puntenlijst.get(spelerbeurt)+1);  //speler die aan beurt is punt bijgeven
                controlleergedaan();                //enkel controleren of spel gedaan is bij juist antwoord
            }

        }

        //SPEL AFRONDEN, SCORES TONEN
        for(int i=0; i<aantalspelers;i++){
            System.out.println("speler score speler " + i+1 + ": " + puntenlijst.get(i));
        }

        return puntenlijst;


    }


}
