package client.view_controllers;

import exceptions.*;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shared_client_appserver_stuff.GameInfo;
import shared_client_appserver_stuff.SpelerInfo;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static client.ClientMainGUI.*;
import static client.utils.Constants.*;

/**
 * Created by ruben on 2/11/18.
 */
public class GameController implements EventHandler<Event> {

    @FXML
    public AnchorPane gameGridPane;

    @FXML
    public GridPane playersListPane;

    @FXML
    public Label playersCount;

    @FXML
    public Label createdBy;

    @FXML
    public Label createdOn;

    @FXML
    public AnchorPane players;

    private GameInfo gameInfo;
    private List<GridPane> spelersRij;
    private GridPane gameBord;
    private Image back = new Image(REVERSE_SIDE);
    private Map<Integer, String> pictures;
    private double boardwidth;
    private double boardLength;
    private Map<String, Label> scores;


    @FXML
    public void exit(){
        try {
            serverImpl.unRegisterWatcher(token, gameId);
            gameController = null;
            Platform.exit(); //TODO als game sluit => alles sluiten?? eerste afmelden!!
            System.exit(0);
        } catch (ConnectException e) {
            e.printStackTrace();
            try {
                renewAppServer();
                exit();
            } catch (NoServerAvailableException e1) {
                e1.printStackTrace();
            }
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize(){

        gameController = this;
        scores = new HashMap<>();

        try {
            gameInfo = serverImpl.getGameForPlaying(token, gameId, false);

            if(gameInfo == null) {
                back();
                return;
            }

            pictures = THEMA_NUMBER.get(gameInfo.getThema());

            //verbind met juiste server
            ServerInfo serverInfo = gameInfo.getServerInfo();
            if(serverInfo.getId() != ID_SERVER) {
                int id = serverInfo.getId();
                serverInfo = connectToAppServer(serverInfo);
                if (serverInfo.getId() != id) {
                    gameInfo = serverImpl.getGameForPlaying(token, gameId, true);
                }
            }


            //spelers kolom
            spelersRij = new ArrayList<>();
            playersCount.setText(Integer.toString(gameInfo.getAantalSpelers()));
            createdBy.setText(gameInfo.getCreator());
            createdOn.setText(gameInfo.getCreateDate());
            List<SpelerInfo> spelers = gameInfo.getSpelers();

            for(int i = 0; i<spelers.size(); i++){
                SpelerInfo speler = spelers.get(i);
                Label username = new Label(speler.getUsername());
                Label score = new Label(Integer.toString(speler.getGameScore()));
                GridPane gridPane = new GridPane();
                scores.put(speler.getUsername(), score);
                gridPane.add(username, 0, 0); //i+1 want kolom title staat op 0
                gridPane.add(score, 1, 0); //i+1 want kolom title staat op 0
                playersListPane.add(gridPane, 0, i+1);
                spelersRij.add(gridPane);
            }

            //zet speler aan beurt in kadertje
            if(gameInfo.isStarted()){
                int spelersBeurt = gameInfo.getSpelersBeurt();
                setBorder(spelersBeurt, true); //+1 want eerste index is kolom title
            }

            //bord toevoegen
            gameBord = new GridPane();
            gameGridPane.getChildren().add(gameBord);

            for(int i = 0; i<gameInfo.getBreedte(); i++){
                for(int j = 0; j<gameInfo.getLengte(); j++){
                    int bord = gameInfo.getBord()[j][i];
                    if(bord == -1) {
                        gameBord.add(spitImageView(back), i, j);
                    } else {
                        gameBord.add(spitImageView(new Image(pictures.get(bord))), i, j);
                    }

                }
            }
            serverImpl.registerWatcher(token, gameId);
            serverImpl.checkUpperGamesCount();

        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            e.printStackTrace();
            try {
                renewAppServer();
                initialize();
            } catch (NoServerAvailableException e1) {
                e1.printStackTrace();
            }
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ImageView spitImageView(Image image){
        //maten uitrekenen voor kotjes
        boardwidth = primaryStage.getWidth() - players.getWidth() - 50 - 350;
        boardLength = players.getHeight() - 50 + 570; //TODO
        /*boardwidth = gameGridPane.getWidth();
        boardLength = gameGridPane.getHeight();*/

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
                if(gameInfo.getBord()[row][column] == -1)
                    serverImpl.flipCard(token, gameId, row, column);
            }
            else{
                throw new PlayerNotInGameException("U speelt niet mee in deze game.");
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            try {
                renewAppServer();
                handle(event);
            } catch (NoServerAvailableException e1) {
                e1.printStackTrace();
            }
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
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateBord(GameInfo gameInfo){
        if(gameInfo.getGameId() != gameId)
            return;

        setBorder(gameInfo.getSpelersBeurt(), true);
        int[][] bord = gameInfo.getBord();

        //set score
        for(SpelerInfo spelerInfo: gameInfo.getSpelers())
            scores.get(spelerInfo.getUsername()).setText(Integer.toString(spelerInfo.getGameScore()));

        ObservableList<Node> nodes = gameBord.getChildren();
        for(Node node: nodes){
            ImageView imageView = (ImageView) node;
            int i = GridPane.getColumnIndex(node);
            int j = GridPane.getRowIndex(node);
            if(bord[j][i] == -1) {
                imageView.setImage(back);
            }
            else {
                imageView.setImage(new Image(pictures.get(bord[j][i])));
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

    @FXML
    public void back(){
        try {
            serverImpl.unRegisterWatcher(token, gameId);
            gameController = null;
            setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
        } catch (ConnectException e) {
            e.printStackTrace();
            try {
                renewAppServer();
                back();
            } catch (NoServerAvailableException e1) {
                e1.printStackTrace();
            }
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void stop(){
        try {
            serverImpl.unRegisterWatcher(token, gameId);
            serverImpl.deleteGame(gameId);
            gameController = null;
            setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
            serverImpl.checkLowerGamesCount(); //niet netjes!!!!!
        } catch (ConnectException e) {
            e.printStackTrace();
            try {
                renewAppServer();
                stop();
            } catch (NoServerAvailableException e1) {
                e1.printStackTrace();
            }
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
