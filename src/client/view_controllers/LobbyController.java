package client.view_controllers;

import static client.ClientMainGUI.*;
import static client.Utils.Constants.LOBBY_COLUMN_NUMBER;
import static client.Utils.Constants.LOBBY_GAME_TILE;

import client_appserver.GameInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import exceptions.NoValidTokenException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ruben on 28/10/18.
 */
public class LobbyController {
    private List<GameInfo> activeGames;

    @FXML
    public GridPane gameGrid;

    @FXML
    public void initialize(){
        try {
            activeGames = impl.getActiveGamesList(token);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(LOBBY_GAME_TILE));
            Parent tile = loader.load();

            for(int i = 0; i<activeGames.size(); i++){
                GameInfo gameInfo = activeGames.get(i);
                gameGrid.add(tile, i/LOBBY_COLUMN_NUMBER, i%LOBBY_COLUMN_NUMBER); //TODO: check of automatisch rijen aanmaakt als index te groot word
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
