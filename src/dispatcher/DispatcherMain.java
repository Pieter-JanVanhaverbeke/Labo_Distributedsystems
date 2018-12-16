package dispatcher;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DispatcherMain {

    private static final int PORT_DISPATCHER = 12345;

    private void startDispatcher() {
        try {
            Registry registry = LocateRegistry.createRegistry(PORT_DISPATCHER);
            registry.rebind("DispatcherImplService", new DispatcherImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Dispatcher is ready");
    }

    public static void main(String[] args) {
        DispatcherMain dispatcherMain = new DispatcherMain();
        dispatcherMain.startDispatcher();

    }

}
