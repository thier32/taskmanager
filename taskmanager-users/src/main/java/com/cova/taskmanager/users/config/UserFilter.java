package com.cova.taskmanager.users.config;

import com.cova.taskmanager.users.services.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
public class UserFilter extends OncePerRequestFilter {


    private final IUserService userService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public UserFilter(IUserService userService) {
        this.userService = userService;
    }


    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // Skip execution completely for auth, swagger, and static resources
        return path.startsWith("/auth/") ||
                path.startsWith("/api/auth/") ||
                pathMatcher.match("/h2-console/**", path) ||
                pathMatcher.match("/v3/api-docs/**", path) ||
                pathMatcher.match("/swagger-ui/**", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Apply admin restriction only when accessing secured business routes, not public auth routes
            String requestURI = request.getRequestURI();

            if (request.isUserInRole("ROLE_ADMIN") && !requestURI.contains("/admin") && !requestURI.startsWith("/auth/")) {
                // Send proper HTTP error response instead of throwing raw exception in filter chain
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin users are restricted to /admin routes");
                return;
            }
        }

        /**/
        /*final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        if (!jwtTokenUtil.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }*/

        // Get user identity and set it on the spring security context
        //String[] credentials = new String[]{username,password};
       // UserDetails userDetails = userService.loadUserByCredentials(credentials);
        /*UserDetails userDetails = userRepo
                .findByUsername(jwtTokenUtil.getUsername(token))
                .orElse(null);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ?
                        List.of() : userDetails.getAuthorities()
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);*/
//        UserDetails userDetails = utilisateurService.loadUserByCredentials(credentials);
//        UsernamePasswordAuthenticationToken
//                authentication = new UsernamePasswordAuthenticationToken(
//                userDetails, null,
//                userDetails == null ?
//                        List.of() : userDetails.getAuthorities()
//        );
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        //this.utilisateurService.loadUserByUsername();
        filterChain.doFilter(request, response);
    }
}
