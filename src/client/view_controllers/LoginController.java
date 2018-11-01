package client.view_controllers;

import exceptions.LoginFailedException;
import exceptions.UserDoesNotExistException;
import exceptions.UsernameAlreadyInUseException;
import exceptions.WrongPasswordException;
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
            usernameLogedIn = username.getText();
            setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
        } catch (RemoteException e) {
            e.printStackTrace(); //TODO: error overlay tonen
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (UserDoesNotExistException e) {
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
