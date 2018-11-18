package client;

import client.view_controllers.ErrorController;
import client.view_controllers.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared_client_appserver_stuff.rmi_int_client_appserver;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import static client.utils.Constants.*;

/**
 * Created by ruben on 26/10/18.
 */
public class ClientMainGUI extends Application {
    //TODO logout on exit!!!
    public static rmi_int_client_appserver impl;
    public static String token;
    public static int gameId; //heeft value geopende game
    public static String usernameLogedIn;

    public static Stage primaryStage;
    public static Stage errorWindow;             geert  
    private static FXMLLoader loader;

    private static final String ADDRESSSERVER = "localhost";
    private static final int PORTSERVER = 10001;

    public static GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        ClientMainGUI.primaryStage = primaryStage;
        primaryStage.setTitle("Memory");

        setScene(LOGIN_SCENE, 800, 500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            serverConnection();
            launch(args);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static void serverConnection() throws RemoteException, NotBoundException {
        Registry registryServer = LocateRegistry.getRegistry(ADDRESSSERVER, PORTSERVER);
        impl = (rmi_int_client_appserver) registryServer.lookup("ServerImplService");
        System.out.println("Server connection ok");
    }

    public static void setScene(String scenePath, int width, int height) {
        try {
            loader = new FXMLLoader();
            loader.setLocation(ClientMainGUI.class.getResource(scenePath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height); //TODO: scene telkens afsluiten als nieuwe scene toon? of allemaal op stack zetten?
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildErrorWindow(String title, String message, boolean error, List<Button> options, Modality modality){
        try {
            //scene
            loader = new FXMLLoader();
            loader.setLocation(ClientMainGUI.class.getResource(ERROR_SCENE));
            Parent root = loader.load();
            ErrorController errorController = loader.getController();
            Scene scene = new Scene(root, ERROR_WIDTH, ERROR_HEIGHT);
            errorWindow = new Stage();
            errorWindow.setTitle(title);
            errorWindow.setScene(scene);
            errorWindow.initModality(modality);
            errorWindow.initOwner(primaryStage);
            errorWindow.setX(primaryStage.getX() + 200);
            errorWindow.setY(primaryStage.getY() + 100);

            //scene content
            errorController.setError(error);
            errorController.setMessage(message);
            errorController.setTitle(title);
            errorController.setOptionButtons(options);

            errorWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
