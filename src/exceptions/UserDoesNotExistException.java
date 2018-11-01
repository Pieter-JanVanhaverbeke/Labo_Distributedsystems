package exceptions;

/**
 * Created by ruben on 1/11/18.
 */
public class UserDoesNotExistException extends Exception {

    public UserDoesNotExistException(String s){
        super(s);
        System.out.println(s);
    }
}
