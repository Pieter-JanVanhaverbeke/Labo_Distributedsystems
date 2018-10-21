package rmi_interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServer extends Remote {

    boolean RegistrerNewClient(String username, String gebruikersnaam) throws RemoteException;

    boolean LogIn(String username, String gebruikersnaam) throws RemoteException;




}
