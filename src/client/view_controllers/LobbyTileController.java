package client.view_controllers;

import client.ClientMainGUI;
import exceptions.GameAlreadyStartedException;
import exceptions.InternalException;
import exceptions.NoValidTokenException;
import exceptions.PlayerNumberExceededException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.rmi.RemoteException;

import static client.utils.Constants.*;
import static client.ClientMainGUI.*;


/**
 * Created by ruben on 1/11/18.
 */
public class LobbyTileController {

    @FXML
    public Label created;

    @FXML
    public Label creator;

    @FXML
    public Label joinedPlayers;

    @FXML
    public Label players;

    @FXML
    public Button tileKnop;

    @FXML
    public ImageView themeImage;

    private int gameId;

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
                catch(RemoteException e){
                    e.printStackTrace();
                } catch (InternalException e) {
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
                } catch (InternalException e) {
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
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InternalException e) {
                    e.printStackTrace();
                }
                break;

            case RESUME_GAME:
                ClientMainGUI.gameId = gameId;
                setScene(OPEN_GAME, GAME_WIDTH, GAME_HEIGHT);
                break;

            case WATCH_GAME:
                ClientMainGUI.gameId = gameId;
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
        return created.getText();
    }

    public void setCreated(String created) {
        this.created.setText(created);
    }

    public String getCreator() {
        return creator.getText();
    }

    public void setCreator(String creator) {
        this.creator.setText(creator);
    }

    public String getJoinedPlayers() {
        return joinedPlayers.getText();
    }

    public void setJoinedPlayers(String joinedPlayers) {
        this.joinedPlayers.setText(joinedPlayers);
    }

    public String getPlayers() {
        return players.getText();
    }

    public void setPlayers(String players) {
        this.players.setText(players);
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setThemeImage(int theme) {
        themeImage.setImage(new Image(BASE_IMG_NUMBER.get(theme)));
    }
}
