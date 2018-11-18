package client.view_controllers;

import client.ClientUpdaterImpl;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
            //hash + salt
            byte[] salt = impl.getSalt(username.getText());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] passwdHash = digest.digest(password.getText().getBytes(StandardCharsets.UTF_8));


            token = impl.logIn(username.getText(), passwdHash, new ClientUpdaterImpl());
            usernameLogedIn = username.getText();
            setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
        } catch (RemoteException e) {
            e.printStackTrace(); //TODO: error overlay tonen
        } catch (UserDoesNotExistException e) {
            Button button = new Button(OK);
            button.setOnAction(actionEvent -> errorWindow.close());
            List<Button> options = new ArrayList<>();
            options.add(button);
            buildErrorWindow(USERNAME_DOES_NOT_EXIST_TITLE, USERNAME_DOES_NOT_EXIST_MESSAGE, false, options, Modality.WINDOW_MODAL);
            e.printStackTrace();
        } catch (WrongPasswordException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void register(){
        setScene(REGISTER_SCENE, LOGIN_WIDTH, LOGIN_HEIGHT);
    }


    @FXML
    public void exit(){
        Platform.exit();
        System.exit(0);
    }

}



