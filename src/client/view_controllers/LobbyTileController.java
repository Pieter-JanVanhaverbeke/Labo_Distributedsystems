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
import shared_client_appserver_stuff.GameInfo;

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
                    serverImpl.startGame(gameId, token);
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
                    serverImpl.joinGame(gameId, token);
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
                    serverImpl.unJoinGame(gameId, token);
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

        try {
            GameInfo gameInfo = serverImpl.getGame(token, gameId);
            fillTile(gameInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    public void fillTile(GameInfo gameInfo){
        int maxAantalSpelers = gameInfo.getAantalSpelers();
        int currentAantalSpelers = gameInfo.getSpelers().size();
        boolean joined = gameInfo.getSpelers().stream().anyMatch(e -> e.getUsername().equals(usernameLogedIn));

        setCreated(gameInfo.getCreateDate());
        setCreator(gameInfo.getCreator());
        setPlayers(Integer.toString(maxAantalSpelers));
        setJoinedPlayers(Integer.toString(currentAantalSpelers));
        setGameId(gameInfo.getGameId()); //enkel nuttig de eerste keer
        setThemeImage(gameInfo.getThema());

        //set button text (join/start/unjoin/resume/watch)
        if(currentAantalSpelers < maxAantalSpelers){
            if(joined)
                setTileKnopTekst(UNJOIN_GAME);
            else
                setTileKnopTekst(JOIN_GAME);
        }
        else{
            if(joined) {
                if (gameInfo.isStarted())
                    setTileKnopTekst(RESUME_GAME);
                else
                    setTileKnopTekst(START_GAME);
            }
            else{
                setTileKnopTekst(WATCH_GAME);
            }
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
