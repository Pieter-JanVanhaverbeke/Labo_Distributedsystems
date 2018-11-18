package client.view_controllers;

import client.ClientUpdaterImpl;
import exceptions.UsernameAlreadyInUseException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
        if(password.getText().equals(password1.getText())) {
            try {
                //hash + salt
                SecureRandom random = new SecureRandom();
                byte salt[] = new byte[20];
                random.nextBytes(salt);

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(salt);
                byte[] encodedhash = digest.digest(password.getText().getBytes(StandardCharsets.UTF_8));

                token = impl.registrerNewClient(username.getText(), encodedhash, salt, new ClientUpdaterImpl());
                usernameLogedIn = username.getText();
                setScene(LOBBY_SCENE, LOBBY_WIDTH, LOBBY_HEIGHT);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (UsernameAlreadyInUseException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
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
