package com.example.AirbnbBooking.configuration.security;

import com.example.AirbnbBooking.models.RoleMaster;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private Long jwtExpire;

    private SecretKey getSecretKey() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String email, Set<RoleMaster> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", roles.stream().map(RoleMaster::getRole).toList());
        return createToken(claims);
    }

    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(claims.get("email").toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpire))
                .signWith(getSecretKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).get("email").toString();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
        } catch(Exception e) {
            throw new RuntimeException("Token is invalid!!");
        }
    }

    public Date extractExpired(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpired(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            boolean isTokenExpired = isTokenExpired(token);
            boolean userNameMatches = userDetails.getUsername().equals(email);

           return email!=null && !isTokenExpired && userNameMatches;

        } catch (Exception e){
            throw new RuntimeException("Token is invalid!!");
        }
    }

}
