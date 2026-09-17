package com.cova.taskmanager.users.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    // Must be at least 48 characters long for HS384 (384 bits)
    @Value("${app.jwt.secret}")
    public String SECRET;
    //private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Endpoints that should completely bypass this filter
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/**",
            "/api/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/h2-console/**",
            "/error"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return EXCLUDED_PATHS.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HEADER);

        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(PREFIX.length());

        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = validateToken(jwtToken);

                if (claims.get(JwtUtils.AUTHORITIES_KEY) != null) {
                    setUpSpringAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
            chain.doFilter(request, response);
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get(JwtUtils.AUTHORITIES_KEY, List.class);

        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList(); // Java 21 stream collection

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                grantedAuthorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}