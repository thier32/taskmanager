package com.cova.taskmanager.users.config;

import com.frame.base.utils.DateUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtUtils {

    // Must be at least 48 characters long for HS384 (384 bits)
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public final static String AUTHORITIES_KEY =  "authorities";

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date now = DateUtils.nowGmt();
        Date expiration = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenFromUsername(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }
}