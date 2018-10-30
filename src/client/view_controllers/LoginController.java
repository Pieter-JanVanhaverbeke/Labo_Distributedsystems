package client.view_controllers;

import exceptions.LoginFailedException;
import exceptions.UsernameAlreadyInUseException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Observable;

import static client.ClientMainGUI.*;
import static client.Utils.Constants.*;

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
            setScene(LOBBY_SCENE, 1300, 700);
        } catch (RemoteException e) {
            e.printStackTrace(); //TODO: error overlay tonen
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void register(){
        setScene(REGISTER_SCENE, LOGIN_WIDTH, LOGIN_HEIGHT);
    }
}
