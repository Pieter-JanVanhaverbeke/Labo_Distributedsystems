package exceptions;

/**
 * Created by ruben on 11/12/18.
 */
public class NoServerAvailableException extends Exception {

    public NoServerAvailableException(String message){
        super(message);
        System.out.println(message);
    }
}
