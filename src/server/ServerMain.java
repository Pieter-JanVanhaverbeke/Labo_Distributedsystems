package server;

import DbConnection.dbConnection;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Set;

public class ServerMain {
    private void startServer() {
        try {
            //create on ort 1099
            Registry registry = LocateRegistry.createRegistry(10001);

            //create a new service named CounterService

            registry.rebind("ServerImplService", new ServerImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public static void main(String[] args) {
        ServerMain main = new ServerMain();
        main.startServer();
     //   dbConnection.connect();
     //   dbConnection.selectAll();
      //  Set<String> set = dbConnection.getUserSet();

      /*  Iterator iter = set.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
*/
    }
}
