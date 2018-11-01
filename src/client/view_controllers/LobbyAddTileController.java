package client.view_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static client.ClientMainGUI.setScene;
import static client.Utils.Constants.CREATE_GAME;
import static client.Utils.Constants.CREATE_GAME_HEIGHT;
import static client.Utils.Constants.CREATE_GAME_WIDTH;

/**
 * Created by ruben on 1/11/18.
 */
public class LobbyAddTileController {

    public void createGame(){
        setScene(CREATE_GAME, CREATE_GAME_WIDTH, CREATE_GAME_HEIGHT);
    }


}
