package client.view_controllers;

import exceptions.LoginFailedException;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static client.ClientMainGUI.*;
import static client.utils.Constants.*;

/**
 * Created by ruben on 28/10/18.
 */
public class LoginController extends Observable {

    @FXML
    public TextField username;

    @FXML
    public TextField password; //TODO: hashing + salt!!!


    @FXML
    public void login(){
        try {
            token = impl.logIn(username.getText(), password.getText());
            usernameLogedIn = username.getText();
            setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
        } catch (RemoteException e) {
            e.printStackTrace(); //TODO: error overlay tonen
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (UserDoesNotExistException e) {
            Button button = new Button(OK);
            button.setOnAction(actionEvent -> errorWindow.close());
            List<Button> options = new ArrayList<>();
            options.add(button);
            buildErrorWindow(USERNAME_DOES_NOT_EXIST_TITLE, USERNAME_DOES_NOT_EXIST_MESSAGE, false, options, Modality.WINDOW_MODAL);
            e.printStackTrace();
        } catch (WrongPasswordException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void register(){
        setScene(REGISTER_SCENE, LOGIN_WIDTH, LOGIN_HEIGHT);
    }
}
