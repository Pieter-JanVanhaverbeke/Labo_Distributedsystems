package exceptions;

/**
 * Created by ruben on 10/11/18.
 */
public class InternalException extends Exception {

    public InternalException(String message){
        super(message);
        System.out.println(message);
    }
}
