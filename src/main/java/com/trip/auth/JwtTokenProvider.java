package com.trip.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    String base64SecretKey = Base64.getEncoder().encodeToString("YourSuperSecretKey1234567890123456".getBytes());
    //    String base64SecretKey = System.getenv("JWT_SECRET_KEY");
    byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
    private final Key key = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());

    private final long accessTokenValidity = 1000 * 60 * 15;
    private final long refreshTokenValidity = 1000 * 60 * 15 * 24 * 7;

    public String generateAccessToken(Long idx) {
        return Jwts.builder()
                .setSubject(idx.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long idx) {
        return Jwts.builder()
                .setSubject(idx.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid JWT");
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
