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

        //        UserService userService = new UserService();
        System.out.println("Requête"+request);

        if(SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)
        ){
            if (request.isUserInRole("ROLE_ADMIN") && !request.getRequestURI().contains("/admin")){

                throw new BadCredentialsException("Invalids credentials");
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
