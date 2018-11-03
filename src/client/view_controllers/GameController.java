package client.view_controllers;

import exceptions.NoValidTokenException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.GameUpdate;
import shared_client_appserver_stuff.SpelerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static client.ClientMainGUI.*;
import static client.utils.Constants.REVERSE_SIDE;
import static client.utils.Constants.SELECTED_BORDER_WIDTH;
import static client.utils.Constants.THEMA_NUMBER;

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

    private GameInfo gameInfo;
    private List<GridPane> spelersRij;
    private GridPane gameBord;
    private Image back = new Image(REVERSE_SIDE);
    ImageView imageView = new ImageView(back);
    private Map<Integer, String> pictures;


    @FXML
    public void initialize(){
        //maten uitrekenen voor kotjes
        double boardwidth = gameGridPane.getWidth();
        double boardLength = gameGridPane.getHeight();
        imageView.setFitHeight(boardLength/gameInfo.getLengte());
        imageView.setFitWidth(boardwidth/gameInfo.getBreedte());

        try {
            gameInfo = impl.getGame(token, gameId);
            pictures = THEMA_NUMBER.get(gameInfo.getThema());

            //bord
            gameBord = new GridPane();
            gameGridPane.getChildren().add(gameBord);

            for(int i = 0; i<gameInfo.getBreedte(); i++){
                for(int j = 0; j<gameInfo.getLengte(); j++){
                    imageView.setImage(back);
                    gameBord.add(imageView, j, i);
                }
            }

            //spelers kolom
            spelersRij = new ArrayList<>();
            playersCount = gameInfo.getAantalSpelers();
            createdBy = gameInfo.getCreator();
            createdOn = gameInfo.getCreateDate();
            List<SpelerInfo> spelers = gameInfo.getSpelers();

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

            //zet speler aan beurt in kadertje
            if(gameInfo.isStarted()){
                int spelersBeurt = gameInfo.getSpelersBeurt();
                setBorder(spelersBeurt, true);
            }

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
    }

    public void updateGame(){
        try {
            GameUpdate gameUpdate = impl.gameUpdate(gameId, token);
            setBorder(gameUpdate.getSpelersBeurt(), true);
            updateBord(gameUpdate.getBord());

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
    }


    private void updateBord(int[][] bord){
        for(int i = 0; i<gameInfo.getBreedte(); i++){
            for(int j = 0; j<gameInfo.getLengte(); j++){
                if(bord[j][i] == -1) {
                    imageView.setImage(back);
                    gameBord.add(imageView, j, i);
                }
                else {
                    imageView.setImage(new Image(pictures.get(bord[j][i])));
                    gameBord.add(imageView, j, i);
                }
            }
        }
    }

    //zet de border van gridpane aan/uit
    private void setBorder(int index, boolean border){
        spelersRij.forEach(e -> e.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0)))));
        if(border){
            spelersRij.get(index).setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(SELECTED_BORDER_WIDTH))));
        }
    }


}
