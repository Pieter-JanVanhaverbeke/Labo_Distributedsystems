package exceptions;

/**
 * Created by ruben on 23/10/18.
 */
public class LoginFailedException extends Exception {

    public LoginFailedException(String reden){
        super(reden);
    }
}
