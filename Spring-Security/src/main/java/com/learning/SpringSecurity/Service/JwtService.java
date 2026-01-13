package com.learning.SpringSecurity.Service;

import com.learning.SpringSecurity.Dao.User;
import com.learning.SpringSecurity.Utility.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.secretKey = generateSecretKey();
    }

    private final JwtProperties jwtProperties;

    private String generateSecretKey() {
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error Generating the Secret Key: " , e);
        }
    }

    public String generateAccessToken(User userDetails){
        Map<String, Object> claims = new HashMap<>();
        return generateToken(userDetails.getUsername(), claims, jwtProperties.getAccessTokenValidity());
    }

    public String generateAccessToken(Map<String, Object> extraClaims, User userDetails){
        return generateToken(userDetails.getUsername(), extraClaims, jwtProperties.getAccessTokenValidity());
    }

    public String generateRefreshToken(User userDetails){
        Map<String, Object> claims = new HashMap<>();
        return generateToken(userDetails.getUsername(), claims, jwtProperties.getAccessTokenValidity());
    }

    public String generateToken(String username, Map<String, Object>claims, Long validity) {
       return Jwts.builder()
               .claims(claims)
               .subject(username)
               .issuedAt(new Date(System.currentTimeMillis()))
               .expiration(new Date(System.currentTimeMillis() + validity))
               .signWith(getKey()).compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(getKey())
//                .build().parseClaimsJws(token).getBody();
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token){
        try{
            return !isTokenExpired(token);
        }catch(Exception e){
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).toInstant().isBefore(new Date( System.currentTimeMillis()).toInstant());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
