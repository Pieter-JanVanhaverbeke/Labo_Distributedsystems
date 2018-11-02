package exceptions;

/**
 * Created by ruben on 2/11/18.
 */
public class GameAlreadyStartedException extends Exception {

    public GameAlreadyStartedException(String message){
        super(message);
        System.out.println(message);
    }
}
