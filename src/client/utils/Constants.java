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

    public static final int GAME_HEIGHT = 700;
    public static final int GAME_WIDTH = 1300;

    public static final int ERROR_HEIGHT = 300;
    public static final int ERROR_WIDTH = 750;

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
    public static final String UNJOIN_GAME = "LEAVE";
    public static final String WATCH_GAME = "WATCH";

    //create game
    public static final double SELECTED_BORDER_WIDTH = 10;

    //game images
    public static final String REVERSE_SIDE = "client/scenes/pictures/reverse.png";

    //exception strings
    public static final String USERNAME_DOES_NOT_EXIST_TITLE = "Username does not exist";
    public static final String OK = "Ok";
    public static final String USERNAME_DOES_NOT_EXIST_MESSAGE = "Username does not exist. Give an existing username.";


    //images en thema namen
    public static final Map<Integer, Map<Integer, String>> THEMA_NUMBER = new HashMap<>();
    public static final Map<Integer, String> BASE_IMG_NUMBER = new HashMap<>();

    public static final Map<Integer, String> PROGRAMMING_LANGUAGES_PICTURES = new HashMap<>();
    public static final Map<Integer, String> BADEEND_PICTURES = new HashMap<>();
    public static final Map<Integer, String> SIMSON_PICTURES = new HashMap<>();

    public static final String THEMA1_NAME = "Programeertalen";
    public static final String THEMA2_NAME = "Badeendjes";
    public static final String THEMA3_NAME = "Simsons";

    public static final String THEMA1_BASE_IMG_PATH = "client/scenes/pictures/game_tile_pictures/programming_languages/java.png";
    public static final String THEMA2_BASE_IMG_PATH = "client/scenes/pictures/game_tile_pictures/badeendjes/duck6.png";
    public static final String THEMA3_BASE_IMG_PATH = "client/scenes/pictures/game_tile_pictures/simpsons/Homer_Simpson.png";

    static {
        THEMA_NUMBER.put(0, PROGRAMMING_LANGUAGES_PICTURES);
        THEMA_NUMBER.put(1, BADEEND_PICTURES);
        THEMA_NUMBER.put(2, SIMSON_PICTURES);

        //paden naar fotos toevoegen in elke map ...
        PROGRAMMING_LANGUAGES_PICTURES.put(0, "client/scenes/pictures/game_tile_pictures/programming_languages/java.png");
        PROGRAMMING_LANGUAGES_PICTURES.put(1, "client/scenes/pictures/game_tile_pictures/programming_languages/c.png");
        PROGRAMMING_LANGUAGES_PICTURES.put(2, "client/scenes/pictures/game_tile_pictures/programming_languages/cpp.png");
        PROGRAMMING_LANGUAGES_PICTURES.put(3, "client/scenes/pictures/game_tile_pictures/programming_languages/csharp.png");
        PROGRAMMING_LANGUAGES_PICTURES.put(4, "client/scenes/pictures/game_tile_pictures/programming_languages/fortran.jpeg");
        PROGRAMMING_LANGUAGES_PICTURES.put(5, "client/scenes/pictures/game_tile_pictures/programming_languages/python.jpeg");
        PROGRAMMING_LANGUAGES_PICTURES.put(6, "client/scenes/pictures/game_tile_pictures/programming_languages/js.png");
        PROGRAMMING_LANGUAGES_PICTURES.put(7, "client/scenes/pictures/game_tile_pictures/programming_languages/cobol.png");

        BADEEND_PICTURES.put(0, "client/scenes/pictures/game_tile_pictures/badeendjes/duck1.png");
        BADEEND_PICTURES.put(1, "client/scenes/pictures/game_tile_pictures/badeendjes/duck2.png");
        BADEEND_PICTURES.put(2, "client/scenes/pictures/game_tile_pictures/badeendjes/duck3.png");
        BADEEND_PICTURES.put(3, "client/scenes/pictures/game_tile_pictures/badeendjes/duck4.png");
        BADEEND_PICTURES.put(4, "client/scenes/pictures/game_tile_pictures/badeendjes/duck5.png");
        BADEEND_PICTURES.put(5, "client/scenes/pictures/game_tile_pictures/badeendjes/duck6.png");
        BADEEND_PICTURES.put(6, "client/scenes/pictures/game_tile_pictures/badeendjes/duck7.png");
        BADEEND_PICTURES.put(7, "client/scenes/pictures/game_tile_pictures/badeendjes/duck8.png");

        SIMSON_PICTURES.put(0, "client/scenes/pictures/game_tile_pictures/simpsons/Bart_Simpson.png");
        SIMSON_PICTURES.put(1, "client/scenes/pictures/game_tile_pictures/simpsons/Homer_Simpson.png");
        SIMSON_PICTURES.put(2, "client/scenes/pictures/game_tile_pictures/simpsons/Krustytheclown.png");
        SIMSON_PICTURES.put(3, "client/scenes/pictures/game_tile_pictures/simpsons/Lisa_Simpson.png");
        SIMSON_PICTURES.put(4, "client/scenes/pictures/game_tile_pictures/simpsons/Maggie_Simpson.png");
        SIMSON_PICTURES.put(5, "client/scenes/pictures/game_tile_pictures/simpsons/Marge_Simpson.png");
        SIMSON_PICTURES.put(6, "client/scenes/pictures/game_tile_pictures/simpsons/Abraham_Simpson.png");
        SIMSON_PICTURES.put(7, "client/scenes/pictures/game_tile_pictures/simpsons/SantasLittleHelper.png");


    }

    static {
        BASE_IMG_NUMBER.put(0, THEMA1_BASE_IMG_PATH);
        BASE_IMG_NUMBER.put(1, THEMA2_BASE_IMG_PATH);
        BASE_IMG_NUMBER.put(2, THEMA3_BASE_IMG_PATH);
    }
}
