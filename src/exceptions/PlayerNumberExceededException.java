package exceptions;

/**
 * Created by ruben on 26/10/18.
 */
public class PlayerNumberExceededException extends Exception {

    public PlayerNumberExceededException(String message){
        super(message);
        System.out.println(message);
    }
}
