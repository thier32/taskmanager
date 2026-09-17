package com.cova.taskmanager.users.config;

import com.cova.taskmanager.users.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig {

    private final CustomAuthenticationManager customAuthenticationManager;
    //private final UserFilter userFilter;


    //public static final String[] SWAGGER_AUTH_WHITELIST = new String[]{
        //    "/swagger-ui.html",
        //    "/swagger-ui/index.html",
        //    "/api-docs",
       //     "/api",
       //     "/auth",
       //     "/api/default",
      //      "/auth/login",
      //      "/auth/register",
      //      "/v3/api-docs",
     //       "/swagger.json",
     //       "/h2-console/**",
     //       "/v3/api-docs*/",
     //       "/api/swagger-config",
     //       "/error",
    //        "/swagger-ui.html", "/webjars/**", "/swagger-resources", "/swagger-resources/**", "/v2/api-docs", "/configuration/ui", "/configuration/security", "/swagger-ui/**", "/v3/api-docs/**"};

    private final IUserService userService;

    // Standard public endpoints for Swagger, H2 Console, and Auth
    public static final String[] SWAGGER_AUTH_WHITELIST = new String[]{
            "/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/h2-console/**",
            "/error"
    };


    @Autowired
    public APIAuthenticationErrEntrypoint apiAuthenticationErrEntrypoint;


    public HttpSecurityConfig(
            CustomAuthenticationManager customAuthenticationManager,
            APIAuthenticationErrEntrypoint apiAuthenticationErrEntrypoint,
            IUserService userService
    ) {
        this.customAuthenticationManager = customAuthenticationManager;
        this.apiAuthenticationErrEntrypoint = apiAuthenticationErrEntrypoint;
        this.userService = userService;
    }

    @Bean
    public UserFilter userFilter(IUserService userService) {
        return new UserFilter(userService);
    }

    @Bean
    RememberMeServices rememberMeServices(IUserService userService) {
        TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("remember-me-new", userService, encodingAlgorithm);
        rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
        return rememberMe;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityApiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/***","/auth/**")
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST APIs
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_AUTH_WHITELIST).permitAll()
                        .requestMatchers("/auth/**", "/api/public/**","/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
               // .authorizeHttpRequests(a ->
               //         a.requestMatchers("/api/swagger-config","/v3/api-docs*/",
               //                         "/v3/api-docs","/auth/login","/auth/register").permitAll()
               //                 .anyRequest().authenticated()
               //         )
                .exceptionHandling((auth) -> {
                    auth.authenticationEntryPoint(apiAuthenticationErrEntrypoint);
                })
                .addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http,RememberMeServices rememberMeServices) throws Exception {
        UserFilter userFilter = new UserFilter(userService);
        http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(
                                "/home",
                                "/search",
                                "/admin/register",
                                "/admin",
                                "/register",
                                "/resources/**",
                                "/resources/static/**",
                                "/js/**",
                                "/css/**",
                                "/api/swagger-config",
                                "/img/**",
                                "/h2-console/**",
                                "/v3/api-docs",
                                "/api",
                                "/error",
                                "/api/login",
                                "/auth/login",
                                "/auth/register"
                        )
                                .permitAll().
                        requestMatchers(SWAGGER_AUTH_WHITELIST).permitAll()
                        //.requestMatchers("/requests/**").permitAll()
                       // .requestMatchers("/**").authenticated()// Allow access to the login page
                        .anyRequest().authenticated()
                        // Secure all other endpoints
                )
                /*.formLogin(form -> form
                        .loginPage("/login") // Use the custom login page
                        .defaultSuccessUrl("/dashboard", true) // Redirect to welcome page after login
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") // Redirect to login page after logout
                        .permitAll()
                )*/
                .rememberMe(
                        rememberMe -> rememberMe.rememberMeServices(rememberMeServices)
                )
                .logout(logout -> logout.deleteCookies("JSESSIONID"))
                .authenticationManager(customAuthenticationManager)
                .addFilterBefore(userFilter, BasicAuthenticationFilter.class);
        return http.build();
    }
}

