package com.cova.taskmanager.users.config;

import com.cova.taskmanager.users.config.exceptions.TaskManagerUserAuthenticationException;
import com.cova.taskmanager.users.services.impl.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {
    private static Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);


    // We are free to implement any authentication logic we want.
    // In our case, we use our existing UserService to load the user data by username, and then we
    // will check if the password, provided in the request, matches the hash of the password from the
    // database
    private final UserService userService;


    private final PasswordEncoder passwordEncoder;


    public CustomAuthenticationManager(UserService userService) {
        this.userService = userService;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info(String.format("Connexion attempt of %s",authentication.getName()));
        UserDetails userCredentialsByUsername =
                userService.loadUserByUsername(authentication.getName());

        if (!passwordEncoder.matches(
                authentication.getCredentials().toString(), userCredentialsByUsername.getPassword())) {

            logger.info(String.format("Bad Connexion attempt of %s",authentication.getName()));
                throw new TaskManagerUserAuthenticationException("Bad credentials");
        }

//        UserDetails authUser =
//                new AuthUser(
//                        userCredentialsByUsername.userResponse().id(),
//                        userCredentialsByUsername.userResponse().roles(),
//                        userCredentialsByUsername.passwordHash());


        logger.info(String.format("Connexion Successfull %s", authentication.getName()));
        // if null would be returned, then another implementation of authentication provider,
        // that support given type of the authentication will be invoked
        return new UsernamePasswordAuthenticationToken(
                userCredentialsByUsername, authentication.getCredentials(), userCredentialsByUsername.getAuthorities());
    }

}
