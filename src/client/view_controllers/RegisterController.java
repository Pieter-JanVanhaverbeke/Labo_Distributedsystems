package client.view_controllers;

import exceptions.UsernameAlreadyInUseException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

import static client.ClientMainGUI.*;
import static client.utils.Constants.*;


/**
 * Created by ruben on 28/10/18.
 */
public class RegisterController {

    @FXML
    public TextField username;

    @FXML
    public TextField password;

    @FXML
    public TextField password1;


    @FXML
    public void createAccount(){
        if(password.equals(password1)) {
            try {
                //TODO: hasing + salt
                token = impl.registrerNewClient(username.getText(), password.getText());
                usernameLogedIn = username.getText();
                setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (UsernameAlreadyInUseException e) {
                e.printStackTrace();
            }
        }
        else{
            //TODO: error bericht
        }
    }

    @FXML
    public void back(){
        setScene(LOGIN_SCENE, LOGIN_WIDTH, LOGIN_HEIGHT);
    }
}
