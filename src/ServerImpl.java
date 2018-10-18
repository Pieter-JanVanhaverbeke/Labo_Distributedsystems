import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements InterfaceServer {

    public ServerImpl() throws RemoteException {        //constructor
    }
}
