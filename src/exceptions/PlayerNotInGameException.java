package exceptions;

/**
 * Created by ruben on 14/11/18.
 */
public class PlayerNotInGameException extends Exception {

    public PlayerNotInGameException(String message){
        super(message);
        System.out.println(message);
    }
}
