package exceptions;

/**
 * Created by ruben on 26/10/18.
 */
public class NotEnoughSpelersException extends Exception{

    public NotEnoughSpelersException(String message){
        super(message);
        System.out.println(message);
    }
}
