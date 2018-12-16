package client;

import client.view_controllers.ErrorController;
import client.view_controllers.GameController;
import client.view_controllers.LobbyController;
import exceptions.NoServerAvailableException;
import exceptions.NoValidTokenException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared_client_appserver_stuff.rmi_int_client_appserver;
import shared_dispatcher_appserver_client_stuff.rmi_int_dispatcher_appserver_client;
import shared_dispatcher_client_stuff.RegisterClientRespons;
import shared_dispatcher_appserver_client_stuff.ServerInfo;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.ConnectException;
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
    public static rmi_int_client_appserver serverImpl;
    public static rmi_int_dispatcher_appserver_client dispatcherImpl;
    public static ClientUpdaterImpl clientUpdater;
    public static String token;
    public static String gameId; //heeft value geopende game
    public static String usernameLogedIn;

    public static Stage primaryStage;
    public static Stage errorWindow;
    private static FXMLLoader loader;

    private static final String ADDRESS_CLIENT = "localhost";
    private static int clientId;
    private static final String ADDRESS_DISPATCHER = "localhost";
    private static final int PORT_DISPATCHER = 12345;
    private static String ADDRESS_SERVER = "localhost";
    public static int PORT_SERVER = 10001;
    public static int ID_SERVER;

    public static GameController gameController;
    public static LobbyController lobbyController;

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
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        }
    }

    private static void serverConnection() throws RemoteException, NotBoundException, NoServerAvailableException {
        //registreer bij de dispatcher en krijg een appserver toegewezen
        Registry registryDispatcher = LocateRegistry.getRegistry(ADDRESS_DISPATCHER, PORT_DISPATCHER);
        dispatcherImpl = (rmi_int_dispatcher_appserver_client) registryDispatcher.lookup("DispatcherImplService");
        RegisterClientRespons respons = dispatcherImpl.registerClient();
        clientId = respons.getId();
        setAddressServer(respons.getServerInfo().getIpAddress());
        setPortServer(respons.getServerInfo().getPortNumber());
        setIdServer(respons.getServerInfo().getId());
        System.out.println("Server: " + ID_SERVER);

        clientUpdater = new ClientUpdaterImpl();

        connect();
    }

    private static void connect(){
        try {
            Registry registryServer = LocateRegistry.getRegistry(ADDRESS_SERVER, PORT_SERVER);
            serverImpl = (rmi_int_client_appserver) registryServer.lookup("ServerImplService");
            System.out.println("Server connection ok");
        }
        catch (ConnectException ce){
            try {
                renewAppServer();
                connect();
            } catch (NoServerAvailableException e) {
                e.printStackTrace();
            }
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void setScene(String scenePath, int width, int height) {
        try {
            lobbyController = null;
            gameController = null;

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

    public static void renewAppServer() throws NoServerAvailableException {
        try {
            System.out.println("bad serverId: " + ID_SERVER);
            ServerInfo serverInfo = dispatcherImpl.reportBadAppServer(ID_SERVER);
            connectToAppServer(serverInfo);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static ServerInfo connectToAppServer(ServerInfo serverInfo){
        try {
            setIdServer(serverInfo.getId());
            setPortServer(serverInfo.getPortNumber());
            setAddressServer(serverInfo.getIpAddress());
            System.out.println("Nieuwe server: " + ID_SERVER);

            Registry registryServer = LocateRegistry.getRegistry(ADDRESS_SERVER, PORT_SERVER);
            serverImpl = (rmi_int_client_appserver) registryServer.lookup("ServerImplService");
            serverImpl.registerClient(token, clientUpdater);

            //als game aant spelen was => terug registeren zodat updates krijgt van bord
            if (gameController != null)
                serverImpl.registerWatcher(token, gameId);

            System.out.println("Server connection ok");
            if(lobbyController != null)
                lobbyController.initialize();

        }catch (ConnectException c){
            c.printStackTrace();
            try {
                return dispatcherImpl.reportBadAppServer(ID_SERVER);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NoServerAvailableException e) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoValidTokenException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (NoServerAvailableException e) {
            e.printStackTrace();
        }

        return serverInfo;
    }

    public static synchronized void setAddressServer(String addressServer) {
        ADDRESS_SERVER = addressServer;
    }

    public static synchronized void setPortServer(int portServer) {
        PORT_SERVER = portServer;
    }

    public static synchronized void setIdServer(int id) {
        ID_SERVER = id;
    }
}
