package client.view_controllers;

import client.ClientMainGUI;
import exceptions.GameAlreadyStartedException;
import exceptions.NoValidTokenException;
import exceptions.PlayerNumberExceededException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.rmi.RemoteException;

import static client.Utils.Constants.*;
import static client.ClientMainGUI.*;


/**
 * Created by ruben on 1/11/18.
 */
public class LobbyTileController {

    @FXML
    public String created;

    @FXML
    public String creator;

    @FXML
    public String joinedPlayers;

    @FXML
    public String players;

    @FXML
    public Button tileKnop;

    private String gameId;

    @FXML
    public void buttonPressed(){
        String buttonText = tileKnop.getText(); //TODO mss niet netjes om op tekst uit gui te selecten :/

        switch (buttonText){
            case START_GAME:
                try {
                    impl.startGame(gameId, token);
                    ClientMainGUI.gameId = gameId;
                    setScene(OPEN_GAME, GAME_WIDTH, GAME_HEIGHT);
                } catch (NoValidTokenException e) {
                    e.printStackTrace();
                }
                break;

            case JOIN_GAME:
                try {
                    impl.joinGame(gameId, token);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NoValidTokenException e) {
                    e.printStackTrace();
                } catch (PlayerNumberExceededException e) {
                    e.printStackTrace();
                }
                break;

            case UNJOIN_GAME:
                try {
                    impl.unJoinGame(gameId, token);
                } catch (NoValidTokenException e) {
                    e.printStackTrace();
                } catch (GameAlreadyStartedException e) {
                    e.printStackTrace();
                }
                break;

            case RESUME_GAME:
                ClientMainGUI.gameId = gameId;
                setScene(OPEN_GAME, GAME_WIDTH, GAME_HEIGHT);
                break;

            case WATCH_GAME:
                ClientMainGUI.gameId = null;
                setScene(OPEN_GAME, GAME_WIDTH, GAME_HEIGHT);
                break;
        }
    }

    public String getTileKnopTekst() {
        return tileKnop.getText();
    }

    public void setTileKnopTekst(String tileKnop) {
        this.tileKnop.setText(tileKnop);
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getJoinedPlayers() {
        return joinedPlayers;
    }

    public void setJoinedPlayers(String joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
