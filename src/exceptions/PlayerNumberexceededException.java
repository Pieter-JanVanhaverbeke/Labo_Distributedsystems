package exceptions;

/**
 * Created by ruben on 26/10/18.
 */
public class PlayerNumberexceededException extends Exception {

    public PlayerNumberexceededException(String message){
        super(message);
        System.out.println(message);
    }
}
