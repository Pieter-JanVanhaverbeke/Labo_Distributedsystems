package exceptions;

/**
 * Created by ruben on 1/11/18.
 */
public class WrongPasswordException extends Exception {

    public WrongPasswordException(String s) {
        super(s);
        System.out.println(s);
    }
}
