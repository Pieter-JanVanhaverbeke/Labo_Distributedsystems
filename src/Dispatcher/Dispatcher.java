package Dispatcher;

import application_server.ServerImpl;
import application_server.ServerMain;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class Dispatcher {
    private int aantalgames;
    private List<ServerMain> serverlijst;       //TODO veranderen naar id ofzo
    private static int serverteller = 0;

    public int getAantalgames() {
        return aantalgames;
    }

    public void setAantalgames(int aantalgames) {
        this.aantalgames = aantalgames;
    }

    public List<ServerMain> getServerlijst() {
        return serverlijst;
    }

    public void setServerlijst(List<ServerMain> serverlijst) {
        this.serverlijst = serverlijst;
    }

    public int getServerteller() {
        return serverteller;
    }

    public void setServerteller(int serverteller) {
        this.serverteller = serverteller;
    }

    public void addAppServer(ServerImpl server){
        ServerMain serverMain = new ServerMain();
        serverMain.setServerid(serverteller);
        serverMain.startServer();
        serverlijst.add(serverMain);
    }


    public void pingServer(String ipadres){
        try{
            InetAddress address = InetAddress.getByName(ipadres);
            boolean reachable = address.isReachable(10000);

            System.out.println("Is host reachable? " + reachable);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void RegisterService(){

    }

    public void UnregisterService(){

    }



}
