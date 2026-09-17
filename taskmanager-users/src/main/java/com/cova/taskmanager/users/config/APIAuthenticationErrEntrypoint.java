package com.cova.taskmanager.users.config;

import com.cova.taskmanager.users.dto.user.UserDeniedResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class APIAuthenticationErrEntrypoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse httpServletResponse, AuthenticationException authException) throws IOException, ServletException {
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        httpServletResponse.setStatus(200);
        Map<String, Object> response = Map.of("message", "SC_UNAUTHORIZED");
        UserDeniedResultDto userDeniedResultDto = new UserDeniedResultDto(authException.getMessage(),401);
        String responseBody = new ObjectMapper().writeValueAsString(userDeniedResultDto);
        httpServletResponse.getWriter().write(responseBody);
    }
}
