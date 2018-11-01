package client.view_controllers;

import exceptions.GameNotCreatedException;
import exceptions.NoValidTokenException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static client.ClientMainGUI.*;

import static client.Utils.Constants.SELECTED_BORDER_WIDTH;

/**
 * Created by ruben on 1/11/18.
 */
public class CreateGameController implements EventHandler<ActionEvent> {

    @FXML
    public int playersNumber;

    @FXML
    public int boardSize;

    @FXML
    public AnchorPane style1;

    @FXML
    public AnchorPane style2;

    @FXML
    public AnchorPane style3;

    private int style;

    public void create(){
        try {
            impl.createGame(playersNumber, boardSize, token, style);
        } catch (GameNotCreatedException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        }
    }

    public void back(){

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
    public void handle(ActionEvent actionEvent) {
        AnchorPane anchorPane = (AnchorPane) actionEvent.getSource();

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
