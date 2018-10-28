package application_server.Utils;

import exceptions.NoValidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import application_server.memory_spel.Speler;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static application_server.DbConnection.dbConnection.getUser;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;

/**
 * Created by ruben on 23/10/18.
 */
public class Utils {
    private static final String KEY_DATA = "PdIkEzT4E5RK-ZJAAHN@#!!EF684efkdzjlfer!(tfz<xscscdwsxqgf&éasxqspm";
    public static final Key JWT_KEY = new SecretKeySpec(KEY_DATA.getBytes(), 0, 65, SignatureAlgorithm.HS512.getJcaName());
    private HashMap<String, Speler> userTokens = new HashMap<>(); //bevat de huidig uitgeleende tokens ( = aangemelde users)


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
                .signWith(HS512, JWT_KEY) //TODO
                .compact();

    }

    public static String generateGameId(){
        return "Dit is een zeer random gameId: 4."; //TODO via db

    }

    public static Speler validateToken(String token) throws NoValidTokenException {
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(token);
            return getUser(claims.getBody().getId());
        }
        catch (SignatureException se){
            throw new NoValidTokenException("Token is niet valid!");
        }
    }
}
