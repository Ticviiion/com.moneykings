package com.moneykings.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey(){
        byte[] keyBytes  = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String username,long userid){
        return Jwts.builder()
                .setSubject(String.valueOf(userid))
                .claim("username",username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token){
        try {
            final String userId = getUserIdFromToken(token);

            return !isTokenExpired(token);
        }catch (Exception e){
            return false;
        }

    }

    public String  getUserIdFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims,T>claimsResolve) {

       final Claims claims = getAllClaimsFromToken(token);
       return claimsResolve.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token){
        final Date expiration = getExpirationDataFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDataFromToken(String token) {
        return getClaimFromToken(token,Claims ::getExpiration);
    }
}
