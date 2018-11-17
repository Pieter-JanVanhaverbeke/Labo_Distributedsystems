package client.view_controllers;

import exceptions.InternalException;
import exceptions.NoValidTokenException;
import javafx.concurrent.Task;
import javafx.scene.layout.GridPane;
import shared_client_appserver_stuff.GameInfo;

import java.rmi.RemoteException;

import static client.ClientMainGUI.*;

/**
 * Created by ruben on 7/11/18.
 */
public class GameUpdateTask extends Task {
    private GridPane gameBord;
    private GridPane playersListPane;
    private GameController gameController;

    public GameUpdateTask(GridPane gameBord, GridPane playersListPane, GameController gameController){
        this.gameBord = gameBord;
        this.playersListPane = playersListPane;
        this.gameController = gameController;
    }


    @Override
    protected Object call() {
        /*try {
            while(!isCancelled()) {
                GameUpdate gameUpdate = impl.gameUpdate(gameId, token);
                updateValue(gameUpdate);
            }
        } catch (NoValidTokenException e) {
            //TODO: exception afhandelen
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    protected void updateValue(Object o) {
        super.updateValue(o);
        gameController.updateBord((GameInfo) o);
    }


}
