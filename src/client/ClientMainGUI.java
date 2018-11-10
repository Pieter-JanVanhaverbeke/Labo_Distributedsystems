package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    public static rmi_int_client_appserver impl;
    public static String token;
    public static int gameId; //heeft value als mag meedoen in game, anders is -1
    public static String usernameLogedIn;

    private static Stage primaryStage;
    private static FXMLLoader loader;

    @Override
    public void start(Stage primaryStage) {
        ClientMainGUI.primaryStage = primaryStage;
        primaryStage.setTitle("Memory");

        setScene(LOGIN_SCENE, 800, 500);
        primaryStage.show();
    }

    public static void main(String[] args) {
     //   launch(args);

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
        Registry registryServer = LocateRegistry.getRegistry("localhost", 10001);
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

    public Stage buildErrorWindow(String title, Stage parent, Modality modality){
        try {
            loader = new FXMLLoader();
            loader.setLocation(ClientMainGUI.class.getResource(ERROR_SCENE));
            Parent root = loader.load();
            Scene scene = new Scene(root, ERROR_WIDTH, ERROR_HEIGHT);
            Stage window = new Stage();
            window.setTitle(title);
            window.setScene(scene);
            window.initModality(modality);
            window.initOwner(parent);
            window.setX(primaryStage.getX() + 200);
            window.setY(primaryStage.getY() + 100);
            window.show();
            return window;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fillErrorWindow(Stage stage, String text, boolean error,  List<String> options){



    }


}
