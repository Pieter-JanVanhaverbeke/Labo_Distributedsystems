package client.view_controllers;

import exceptions.NoValidTokenException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.SpelerInfo;

import java.util.ArrayList;
import java.util.List;

import static client.ClientMainGUI.*;
import static client.Utils.Constants.SELECTED_BORDER_WIDTH;

/**
 * Created by ruben on 2/11/18.
 */
public class GameController {

    @FXML
    public AnchorPane gameGridPane;

    @FXML
    public GridPane playersListPane;

    @FXML
    public int playersCount;

    @FXML
    public String createdBy;

    @FXML
    public String createdOn;

    private GameInfo game;
    private List<GridPane> spelersRij;
    private GridPane gameBord;

    @FXML
    public void initialize(){
        try {
            gameBord = new GridPane();
            gameGridPane.getChildren().add(gameBord);
            spelersRij = new ArrayList<>();
            game = impl.getGame(token, gameId);
            playersCount = game.getAantalSpelers();
            createdBy = game.getCreator();
            createdOn = game.getCreateDate();
            List<SpelerInfo> spelers = game.getSpelers();

            for(int i = 0; i<spelers.size(); i++){
                SpelerInfo speler = spelers.get(i);
                Label username = new Label(speler.getUsername());
                Label score = new Label(Integer.toString(speler.getGameScore()));
                GridPane g = new GridPane();
                spelersRij.add(g);
                g.add(username, 0, 0);
                g.add(score, 0, 1);
                playersListPane.add(g, i+1, 0); //i+1 want kolom title staat op 0
            }

            if(game.isStarted()){
                int spelersBeurt = game.getSpelersBeurt();
                setBorder(spelersRij.get(spelersBeurt), true);
            }

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
    }


    //zet de border van gridpane aan/uit
    private void setBorder(GridPane gridPane, boolean border){
        if(border){
            gridPane.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(SELECTED_BORDER_WIDTH))));
        }
        else{
            gridPane.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0))));
        }
    }


}
