package client.view_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static client.Utils.Constants.*;

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

    @FXML
    public void buttonPressed(){
        String buttonText = tileKnop.getText();

        switch (buttonText){
            case START_GAME:


                break;

            case JOIN_GAME:


                break;

            case UNJOIN_GAME:


                break;

            case RESUME_GAME:


                break;

            case WATCH_GAME:


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
}
