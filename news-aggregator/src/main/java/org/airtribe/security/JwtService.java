package org.airtribe.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.SQLOutput;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("$jwt.expiration")
    private long jwtExpiration;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userName) {
        return Jwts.builder().setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserName(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey)
                .build().parseClaimsJwt(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJwt(token);
            return true;
        }
        catch (ExpiredJwtException ex) {
            System.out.println("JWT expired: " + ex.getMessage());
        }
        catch (UnsupportedJwtException ex)  {
            System.out.println("Unsupported JWT: " + ex.getMessage());
        }
        catch (MalformedJwtException ex) {
            System.out.println("Malformed JWT: " + ex.getMessage());
        }
        catch (SignatureException ex) {
            System.out.println("Invalid signature: " + ex.getMessage());
        }
        catch (IllegalArgumentException ex) {
            System.out.println("Illegal argument: " + ex.getMessage());
        }
        return false;
    }
}
