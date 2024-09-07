package com.project.shopapp.components;

import com.project.shopapp.exception.InvalidParamsException;
import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.expiration}")
    private int expiration;//save to an enviroment varible
    @Value("${jwt.secretKey}")
    private String secretKey;
    public String generateToken(User user) throws Exception{
        //properties=>claims
        Map<String, Object> claims = new HashMap<>();
        this.generateSecretKet();
        claims.put("phoneNumber", user.getPhoneNumber());
        try {
            String token= Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis()+expiration*1000L))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParamsException("Cannot create jwt token ,error"+e.getMessage());

        }
    }
    private Key getSignKey(){

        byte[] bytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes); 
    }
    private String generateSecretKet(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String secretKey= Encoders.BASE64.encode(bytes);
        return secretKey;
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }
    public  <T> T extractClaim(String token, Function<Claims,T> claimsResolver ){
        final Claims claims=this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //check expiration
    private boolean isTokenExpired(String token){
        Date expirationDate=this.extractClaim(token,Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public String extracPhoneNumber(String token){
        return extractClaim(token,Claims::getSubject);
    }
}
