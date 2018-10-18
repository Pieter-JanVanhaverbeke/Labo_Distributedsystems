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
            System.out.println("Geef een gebruikersnaam in:");
            String username = sc.nextLine();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ClientMain main = new ClientMain();
        main.startClient();
    }

}