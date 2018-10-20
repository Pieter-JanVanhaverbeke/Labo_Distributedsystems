package memory_spel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static memory_spel.Constants.*;

public class GameMain {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Speler> spelers = new ArrayList<>();
    private static Lobby lobby = new Lobby();

    public static void main(String[] args) {
        printMenu();
    }

    private static void printMenu(){
        while (true) {
            System.out.println(
                    "1. User aanmaken\n" +
                            "2. Actieve games in lobby opvragen\n" +
                            "3. Nieuwe game aanmaken\n" +
                            "4. Deelnemen aan bestaande game uit lobby.\n"
            );
            int keuze = -1;
            keuze = Integer.parseInt(scanner.nextLine());
            while (keuze < 0 || keuze > 4) {
                keuze = Integer.parseInt(scanner.nextLine());
                System.out.println("Ongeldige keuze. Probeer opnieuw.");
            }

            switch (keuze) {
                case 1:
                    System.out.println("Geef een username in.");
                    Speler speler = null;
                    speler = new Speler(scanner.nextLine());
                    spelers.add(speler);
                    System.out.println("Speler gecreeêeeëeërd.");
                    break;
                case 2:
                    int i = 1;
                    System.out.println("Lijst van actieve games: ");
                    List<Game> actieveGames = lobby.getActiveGames();
                    for (Game game: actieveGames)
                        System.out.println(i++ + ". " + game);
                    System.out.println();
                    break;
                case 3:
                    int aantalSpelers = -1;
                    while (aantalSpelers > MAX_PLAYER_COUNT || aantalSpelers < MIN_PLAYER_COUNT) {
                        System.out.println("Geef het aantal spelers in.");
                        aantalSpelers = Integer.parseInt(scanner.nextLine());
                    }

                    List<Speler> gameSpelers = new ArrayList<>();
                    for (int j = 0; j < aantalSpelers; j++) {
                        System.out.println("Geef een deelnemende username in.");
                        String name = scanner.nextLine();
                        for (Speler speler1 : spelers) {
                            if (speler1.getUsername().equals(name)) {
                                gameSpelers.add(speler1);
                            }
                        }
                    }

                    int bordGrootte = -1;
                    while (bordGrootte < MIN_BOARD_SIZE || bordGrootte > MAX_BOARD_SIZE) {
                        System.out.println("Geef de bordgrootte in, 1 = small, 2 = medium, 3 = large.");
                        bordGrootte = Integer.parseInt(scanner.nextLine());
                    }

                    boolean created = false;
                    while (!created) {
                        created = lobby.createNewGame(gameSpelers, bordGrootte);
                        System.out.println("Het aantal spelers of de bordgrootte is niet geldig.");
                    }
                    break;
                case 4:
                    System.out.println("Kies een spel om te starten (nummer).");
                    List<Game> actieveGamess = lobby.getActiveGames();
                    int keuzeGame = Integer.parseInt(scanner.nextLine());
                    while (keuzeGame < 1 || keuzeGame > actieveGamess.size()) {
                        keuzeGame = Integer.parseInt(scanner.nextLine());
                        System.out.println("Deze waarde is niet geldig.");
                    }

                    actieveGamess.get(keuzeGame - 1).start();
                    break;
            }
        }

    }
}
