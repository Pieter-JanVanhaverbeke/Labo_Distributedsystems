package application_server.Utils;

import exceptions.NoValidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import application_server.memory_spel.Speler;

import javax.crypto.spec.SecretKeySpec;
import java.rmi.RemoteException;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static application_server.ServerImpl.impl;

/**
 * Created by ruben on 23/10/18.
 */
public class Utils {
    private static final String KEY_DATA = "PdIkEzT4E5RK-ZJAAHN@#!!EF684efkdzjlfer!(tfz<xscscdwsxqgf&éasxqspm";
    public static final Key JWT_KEY = new SecretKeySpec(KEY_DATA.getBytes(), 0, 65, SignatureAlgorithm.HS512.getJcaName());
    //public static final Key JWT_KEY = new SecretKeySpec(KEY_DATA.getBytes(), 0, 16, "AES");

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
    }

    public static Speler validateToken(String token) throws NoValidTokenException, RemoteException {
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(token);
            return impl.getSpeler(claims.getBody().getId());
        }
        catch (SignatureException se){
            throw new NoValidTokenException("Token is niet valid!");
        }
    }
}
