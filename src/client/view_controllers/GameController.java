package client.view_controllers;

import exceptions.*;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.GameUpdate;
import shared_client_appserver_stuff.SpelerInfo;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static client.ClientMainGUI.*;
import static client.utils.Constants.REVERSE_SIDE;
import static client.utils.Constants.SELECTED_BORDER_WIDTH;
import static client.utils.Constants.THEMA_NUMBER;

/**
 * Created by ruben on 2/11/18.
 */
public class GameController implements EventHandler<Event> {

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
    private Map<Integer, String> pictures;
    private double boardwidth;
    private double boardLength;


    @FXML
    public void initialize(){
        //maten uitrekenen voor kotjes
        boardwidth = gameGridPane.getWidth();
        boardLength = gameGridPane.getHeight();

        try {
            gameInfo = impl.getGame(token, gameId);
            pictures = THEMA_NUMBER.get(gameInfo.getThema());

            //bord toevoegen
            gameBord = new GridPane();
            gameGridPane.getChildren().add(gameBord);

            for(int i = 0; i<gameInfo.getBreedte(); i++){
                for(int j = 0; j<gameInfo.getLengte(); j++){
                    gameBord.add(spitImageView(back), j, i);
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

            // start update thread voor game
            // javaFx != thread safe => gebruik gemaakt van concurent package van javaFX
            Task task = new GameUpdateTask(gameBord, playersListPane, this);
            new Thread(task).start();

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    private ImageView spitImageView(Image image){
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(boardLength/gameInfo.getLengte());
        imageView.setFitWidth(boardwidth/gameInfo.getBreedte());
        imageView.setOnMouseClicked(this::handle);
        return imageView;
    }

    @Override
    public void handle(Event event) {
        try {
            if(gameInfo.getSpelers().stream().anyMatch(e -> e.getUsername().equals(usernameLogedIn))) {
                ImageView imageView = (ImageView) event.getSource();
                int column = GridPane.getColumnIndex(imageView);
                int row = GridPane.getRowIndex(imageView);
                impl.flipCard(token, gameId, row, column);
            }
            else{
                throw new PlayerNotInGameException("U speelt niet mee in deze game.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (NotYourTurnException e) {
            e.printStackTrace();
        } catch (NotEnoughSpelersException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (PlayerNotInGameException e) {
            e.printStackTrace();
        }
    }

    public void updateBord(GameUpdate gameUpdate){
        setBorder(gameUpdate.getSpelersBeurt(), true);
        int[][] bord = gameUpdate.getBord();
        for(int i = 0; i<gameInfo.getBreedte(); i++){
            for(int j = 0; j<gameInfo.getLengte(); j++){
                if(bord[j][i] == -1) {
                    gameBord.add(spitImageView(back), j, i);
                }
                else {
                    gameBord.add(spitImageView(new Image(pictures.get(bord[j][i]))), j, i);
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
