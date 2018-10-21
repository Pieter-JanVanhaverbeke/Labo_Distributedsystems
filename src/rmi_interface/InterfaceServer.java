package rmi_interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServer extends Remote {

    void RegistrerNewClient(String username, String gebruikersnaam) throws RemoteException;




}
