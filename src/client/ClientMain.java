package client;

import rmi_interface.InterfaceServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClientMain {
    private void startClient() {
        try {
            // fire to localhost on port 1099
            Registry registryServer = LocateRegistry.getRegistry("localhost", 10001);

            // search interfaceServer

            InterfaceServer impl = (InterfaceServer) registryServer.lookup("ServerImplService");

            // setup scanner for user input, ask for username
            Scanner sc = new Scanner(System.in);

            System.out.println("1: registreer 2:log in");

            if(sc.nextInt()==1){

            System.out.println("Geef een gebruikersnaam in:");

            String username = sc.nextLine();


            System.out.println("geef een wachtwoord in");
            String wachtwoord = sc.nextLine();
            System.out.println("herhaal het wachtwoord ");
            if(!wachtwoord.equals(sc.nextLine())){
                System.out.println("wachtwoord matcht niet");
            }
            else{
                //stuur gegevens naar server

            }


            }

            else {
                System.out.println("Geef een gebruikersnaam in:");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ClientMain main = new ClientMain();
        main.startClient();
    }

}