package exceptions;

/**
 * Created by ruben on 23/10/18.
 */
public class UsernameAlreadyInUseException extends Exception{

    public UsernameAlreadyInUseException(String username){
        super();
        System.out.println("Username: " + username + " is already in use.");
    }
}
