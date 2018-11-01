package client;

import exceptions.*;
import application_server.memory_spel.Game;
import shared_client_appserver_stuff.rmi_int_client_appserver;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

import static application_server.Utils.Constants.*;

public class ClientMainCLI {
    private static String token = null;
    private static String gameId = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static rmi_int_client_appserver impl;

    private void startClient() {
        try {
            Registry registryServer = LocateRegistry.getRegistry("localhost", 10001);
            impl = (rmi_int_client_appserver) registryServer.lookup("ServerImplService");
            printMenu();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ClientMainCLI main = new ClientMainCLI();
        main.startClient();
    }


    private static void printMenu() throws RemoteException {
        while (true) {
            System.out.println(
                    "1. User aanmaken\n" +
                            "2. Actieve games in lobby opvragen\n" +
                            "3. Nieuwe game aanmaken\n" +
                            "4. Deelnemen aan bestaande game uit lobby.\n" +
                            "5. aanmelden.\n" +
                            "6. Log out.\n" +
                            "7. Stop game.\n"
            );

            int keuze = -1;
            keuze = Integer.parseInt(scanner.nextLine());
            while (keuze < 0 || keuze > 7) {
                keuze = Integer.parseInt(scanner.nextLine());
                System.out.println("Ongeldige keuze. Probeer opnieuw.");
            }

            switch (keuze) {
                case 1:
                    System.out.println("Geef een gebruikersnaam in:");
                    String username = scanner.nextLine();

                    System.out.println("Geef een wachtwoord in");
                    String password = scanner.nextLine();

                    System.out.println("herhaal het wachtwoord ");
                    if (!password.equals(scanner.nextLine())) {
                        System.out.println("wachtwoord matcht niet");
                    } else {
                        //stuur gegevens naar application_server
                        try {
                            //na registratie autmatisch aangemeld
                            token = impl.registrerNewClient(username, password); //met exceptions werken ipv return boolean/int? wat denk je?
                            System.out.println("Registratie voltooid");
                        } catch (UsernameAlreadyInUseException e) {
                            System.out.println("Gebruikersnaam is al gebruikt");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2:
                    System.out.println("Lijst van actieve games: ");
                    Map<String, Game> actieveGames = null;
                    try {
                        actieveGames = impl.getActiveGames(token);
                    } catch (NoValidTokenException e) {
                        System.out.println(e.getMessage());
                    }

                    for (String gameId : actieveGames.keySet())
                        System.out.println(gameId + ": " + actieveGames.get(gameId));
                    System.out.println();
                    break;

                case 3:
                    int aantalSpelers = -1;
                    while (aantalSpelers > MAX_PLAYER_COUNT || aantalSpelers < MIN_PLAYER_COUNT) {
                        System.out.println("Geef het aantal spelers in.");
                        aantalSpelers = Integer.parseInt(scanner.nextLine());
                    }

                    int bordGrootte = -1;
                    while (bordGrootte < MIN_BOARD_SIZE || bordGrootte > MAX_BOARD_SIZE) {
                        System.out.println("Geef de bordgrootte in, 1 = small, 2 = medium, 3 = large.");
                        bordGrootte = Integer.parseInt(scanner.nextLine());
                    }

                    try {
                        gameId = impl.createGame(aantalSpelers, bordGrootte, token, 2);
                    } catch (GameNotCreatedException e) {
                        System.out.println("Game creatie mislukt: " + e.getMessage());
                    } catch (NoValidTokenException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    try {
                        System.out.println("Kies een spel om te starten (gameId).");
                        Map<String, Game> actieveGamess = impl.getActiveGames(token); //TODO: nadenken moet een volledig Game object lijst gestuurd worden of enkel metadata over game?
                        String keuzeGame = scanner.nextLine();
                        while (!actieveGamess.containsKey(keuzeGame)) {
                            keuzeGame = scanner.nextLine();
                            System.out.println("Deze waarde is niet geldig. Geef een nieuwe keuze.");
                        }

                        impl.joinGame(actieveGamess.get(keuzeGame).getGameId(), token);
                    } catch (NoValidTokenException e) {
                        System.out.println(e.getMessage());
                    } catch (PlayerNumberExceededException e) {
                        e.printStackTrace();
                    }
                    break;

                case 5:
                    System.out.println("Geef de gebruikersnaam in:");
                    username = scanner.nextLine();

                    System.out.println("Geef het wachtwoord in");
                    password = scanner.nextLine();

                    try {
                        token = impl.logIn(username, password);
                    } catch (LoginFailedException e) {
                        System.out.println("Login mislukt: " + e.getMessage());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case 6:
                    token = null;
                    System.out.println("Uitgelogd");
            }
        }
    }

}