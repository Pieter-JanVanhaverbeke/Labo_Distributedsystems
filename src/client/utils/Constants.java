package client.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruben on 28/10/18.
 */
public class Constants {

    //scene specs
    public static final int LOGIN_WIDTH = 800;
    public static final int LOGIN_HEIGHT = 500;

    public static final int LOBBY_WIDTH = 1300;
    public static final int LOBBY_HEIGHT = 700;

    public static final int CREATE_GAME_WIDTH = 1300;
    public static final int CREATE_GAME_HEIGHT = 700;

    public static final int GAME_HEIGHT = 1300;
    public static final int GAME_WIDTH = 700;

    public static final int ERROR_HEIGHT = 500;
    public static final int ERROR_WIDTH = 300;

    //FXML locations
    public static final String LOGIN_SCENE = "scenes/login.fxml";
    public static final String LOBBY_SCENE = "scenes/lobby.fxml";
    public static final String REGISTER_SCENE = "scenes/register.fxml";
    public static final String LOBBY_GAME_TILE = "scenes/lobby_tile.fxml";
    public static final String LOBBY_ADD_GAME_TILE = "scenes/lobby_tile_create_game.fxml";
    public static final String CREATE_GAME = "scenes/create_game.fxml";
    public static final String OPEN_GAME = "scenes/game.fxml";
    public static final String ERROR_SCENE = "scenes/error.fxml";

    //lobby
    public static final int LOBBY_COLUMN_NUMBER = 4;

    //lobby tiles
    public static final String JOIN_GAME = "JOIN";
    public static final String START_GAME = "START";
    public static final String RESUME_GAME = "RESUME";
    public static final String UNJOIN_GAME = "UNJOIN";
    public static final String WATCH_GAME = "WATCH";

    //create game
    public static final double SELECTED_BORDER_WIDTH = 10;

    //game images
    public static final String REVERSE_SIDE = "client/scenes/pictures/reverse.png";


    public static final Map<Integer, Map<Integer, String>> THEMA_NUMBER = new HashMap<>();
    public static final Map<Integer, String> PROGRAMMING_LANGUAGES = new HashMap<>();
    public static final Map<Integer, String> THEMA2 = new HashMap<>();
    public static final Map<Integer, String> THEMA3 = new HashMap<>();

    static {
        THEMA_NUMBER.put(0, PROGRAMMING_LANGUAGES);
        THEMA_NUMBER.put(1, THEMA2);
        THEMA_NUMBER.put(2, THEMA3);

        //paden naar fotos toevoegen in elke map ...
    }

}
