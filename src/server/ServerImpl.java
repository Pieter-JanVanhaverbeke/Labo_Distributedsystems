package server;

import rmi_interface.InterfaceServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerImpl extends UnicastRemoteObject implements InterfaceServer {
    ArrayList<String> users = new ArrayList<>();
    HashMap<String, String> clientList = new HashMap<String, String>();

    public ServerImpl() throws RemoteException {        //constructor
    }


    @Override
    public void RegistrerNewClient(String username, String password){
        if(clientList.containsKey(username)){
            System.out.println("gebruikersnaam al gebruikt");
        }
        else{
            //Wachtwoord Hashen en naar databank sturen(bij de client hashen)
            clientList.put(username,password);           //later opslaan in databank ipv applicatielaag
            System.out.println("gebruiker: " + username + " aangemaakt!");
        }
   }
}
