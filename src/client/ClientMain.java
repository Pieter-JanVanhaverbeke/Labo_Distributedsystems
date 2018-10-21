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

            int keuze = sc.nextInt();

            if(keuze==1){

                sc.nextLine();      //anders problemen met inlezen
                System.out.println("Geef een gebruikersnaam in:");

                String username = sc.nextLine();

            System.out.println("Geef een wachtwoord in");
            String password = sc.nextLine();

            System.out.println("herhaal het wachtwoord ");

            if(!password.equals(sc.nextLine())){
                System.out.println("wachtwoord matcht niet");
            }
            else{
                impl.RegistrerNewClient(username,password);
                //stuur gegevens naar server


            }


            }

            else {
                System.out.println("Geef een gebruikersnaam in:");
                String username = sc.nextLine();
                String password = sc.nextLine();



                //TRY LOGIN(username, wachtwoord)

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