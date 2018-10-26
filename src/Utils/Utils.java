package Utils;

import exceptions.NoValidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ruben on 23/10/18.
 */
public class Utils {
    private static final String KEY_DATA = "PdIkEzT4E5RK-ZJAAHN@#!!EF684pm";
    public static final Key JWT_KEY = new SecretKeySpec(KEY_DATA.getBytes(), 0, 16, "AES");

    public static String generateUserToken(String username){
        Date curDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.HOUR, 24);

        return Jwts.builder()
                .setId(username)
                .setSubject("DS_Opdracht")
                .setIssuer("Client")
                .setIssuedAt(curDate)
                .setExpiration(cal.getTime())
                .signWith(SignatureAlgorithm.HS512, JWT_KEY) //TODO
                .compact();


        //return "Dit is een zeer random token: pannekoek.";

    }

    public static String generateGameId(){
        return "Dit is een zeer random gameId: 4.";

    }

    public static boolean validateToken(String token) throws NoValidTokenException {
        throw new NoValidTokenException("Token not valid.");
    }

    public static void invalidateToken(String token) {

    }
}
