package appserver_db;

import exceptions.UsernameAlreadyInUseException;
import application_server.memory_spel.Speler;

import java.rmi.Remote;
import java.util.List;

/**
 * Created by ruben on 26/10/18.
 */
public interface rmi_int_appserver_db extends Remote {

    void createUser(String username, String passwdHash) throws UsernameAlreadyInUseException;

    void setUsertoken(Speler speler, String token);

    void invalidateUsertoken(Speler speler);

    List<Speler> getAllSpelers();

    Speler getSpeler(String username);

    void changeCredentials(String username, String passwdHash) throws UsernameAlreadyInUseException; //verander usernamen en password, enkel dingen vervangen die niet null zijn





}
