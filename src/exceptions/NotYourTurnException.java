package exceptions;

/**
 * Created by ruben on 26/10/18.
 */
public class NotYourTurnException extends Exception {

    public NotYourTurnException(String message){
        super(message);
        System.out.println(message);
    }
}
