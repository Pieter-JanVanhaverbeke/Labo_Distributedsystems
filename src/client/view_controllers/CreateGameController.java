package client.view_controllers;

import exceptions.GameNotCreatedException;
import exceptions.InternalException;
import exceptions.NoValidTokenException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.rmi.RemoteException;

import static client.ClientMainGUI.*;
import static client.utils.Constants.*;

/**
 * Created by ruben on 1/11/18.
 */
public class CreateGameController implements EventHandler<MouseEvent> {

    @FXML
    public TextField playersNumber;

    @FXML
    public TextField boardSize;

    @FXML
    public AnchorPane style1;

    @FXML
    public AnchorPane style2;

    @FXML
    public AnchorPane style3;

    private int style;

    @FXML
    public void initialize(){
        GridPane g = (GridPane) style1.getChildren().get(0);
        Label l = (Label) g.getChildren().get(1);
        ImageView img = (ImageView) g.getChildren().get(0);
        l.setText(THEMA1_NAME);
        img.setImage(new Image(BASE_IMG_NUMBER.get(0)));

        g = (GridPane) style2.getChildren().get(0);
        l = (Label) g.getChildren().get(1);
        img = (ImageView) g.getChildren().get(0);
        l.setText(THEMA2_NAME);
        img.setImage(new Image(BASE_IMG_NUMBER.get(1)));

        g = (GridPane) style3.getChildren().get(0);
        l = (Label) g.getChildren().get(1);
        img = (ImageView) g.getChildren().get(0);
        l.setText(THEMA3_NAME);
        img.setImage(new Image(BASE_IMG_NUMBER.get(2)));

    }

    public void create(){
        try {
            serverImpl.createGame(Integer.parseInt(playersNumber.getText()), Integer.parseInt(boardSize.getText()), token, style);
            setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
        } catch (GameNotCreatedException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    public void back(){
        setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
    }

    //zet de border van anchorpane aan/uit
    private void setBorder(AnchorPane anchorPane, boolean border){
        if(border){
            anchorPane.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(SELECTED_BORDER_WIDTH))));
        }
        else{
            anchorPane.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0))));
        }
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        AnchorPane anchorPane = (AnchorPane) mouseEvent.getSource();

        if(anchorPane.equals(style1)){
            style = 0;
            setBorder(style1, true);
            setBorder(style2, false);
            setBorder(style3, false);
        }
        else if(anchorPane.equals(style2)){
            style = 1;
            setBorder(style1, false);
            setBorder(style2, true);
            setBorder(style3, false);
        }
        else if(anchorPane.equals(style3)){
            style = 2;
            setBorder(style1, false);
            setBorder(style2, false);
            setBorder(style3, true);
        }
        else{
            style = -1;
            setBorder(style1, false);
            setBorder(style2, false);
            setBorder(style3, false);
        }
    }
}
