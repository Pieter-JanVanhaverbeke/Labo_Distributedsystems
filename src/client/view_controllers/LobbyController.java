package client.view_controllers;

import static client.ClientMainGUI.*;
import static client.utils.Constants.*;

import client.ClientMainGUI;
import exceptions.InternalException;
import shared_client_appserver_stuff.GameInfo;
import exceptions.NoValidTokenException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

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

    //vul lijst met actieve games die in lobby moeten staan
    @FXML
    public void initialize(){
        try {
            activeGames = impl.getActiveGamesList(token);
            FXMLLoader loader = new FXMLLoader();

            //add existing gametiles
            loader.setLocation(ClientMainGUI.class.getResource(LOBBY_GAME_TILE));
            Parent tile = loader.load(); //TODO: check of niet telkens zelfde instantie in rij komt, if so -> deze lijn in for lus zetten
            LobbyTileController lobbyTileController = loader.getController();

            int i;
            for(i = 0; i<activeGames.size(); i++){
                GameInfo gameInfo = activeGames.get(i);
                int maxAantalSpelers = gameInfo.getAantalSpelers();
                int currentAantalSpelers = gameInfo.getSpelers().size();
                boolean joined = gameInfo.getSpelers().contains(usernameLogedIn);

                lobbyTileController.setCreated(gameInfo.getCreateDate());
                lobbyTileController.setCreator(gameInfo.getCreator());
                lobbyTileController.setPlayers(Integer.toString(maxAantalSpelers));
                lobbyTileController.setJoinedPlayers(Integer.toString(currentAantalSpelers));
                lobbyTileController.setGameId(gameInfo.getGameId());

                //set button text (join/start/unjoin/resume/watch)
                if(currentAantalSpelers < maxAantalSpelers){
                    if(joined)
                        lobbyTileController.setTileKnopTekst(UNJOIN_GAME);
                    else
                        lobbyTileController.setTileKnopTekst(JOIN_GAME);
                }
                else{
                    if(joined) {
                        if (gameInfo.isStarted())
                            lobbyTileController.setTileKnopTekst(RESUME_GAME);
                        else
                            lobbyTileController.setTileKnopTekst(START_GAME);
                    }
                    else{
                        lobbyTileController.setTileKnopTekst(WATCH_GAME);
                    }
                }
                gameGrid.add(tile, i/LOBBY_COLUMN_NUMBER, i%LOBBY_COLUMN_NUMBER); //TODO: check of automatisch rijen aanmaakt als index te groot word
            }

            //add create game tile
            loader = new FXMLLoader();
            loader.setLocation(ClientMainGUI.class.getResource(LOBBY_ADD_GAME_TILE));
            tile = loader.load();
            gameGrid.add(tile, i/LOBBY_COLUMN_NUMBER, i%LOBBY_COLUMN_NUMBER); //TODO: check of automatisch rijen aanmaakt als index te groot word

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout(){
        token = null;
        gameId = -1;
        usernameLogedIn = null;
        setScene(LOGIN_SCENE, LOGIN_WIDTH, LOGIN_HEIGHT);
    }
}
