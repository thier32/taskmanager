package com.cova.taskmanager.users.business;

import com.cova.taskmanager.users.config.JwtUtils;
import com.cova.taskmanager.users.config.exceptions.TaskManagerUserAuthenticationException;
import com.cova.taskmanager.users.dto.user.*;
import com.cova.taskmanager.users.model.User;
import com.cova.taskmanager.users.services.impl.UserService;
import com.cova.taskmanager.users.utils.PasswordChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frame.base.business.BaseBusiness;
import com.frame.base.dto.ResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserBusiness extends BaseBusiness<UserResultDto, UserDto> {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;


    public UserBusiness(UserService userService, ObjectMapper objectMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder
    )
    {
        super(userService);
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseDto login(UserLoginDto loginDto){
        Map<String, Object> map = objectMapper.convertValue(loginDto, Map.class);
        User user = (User)((UserService)this.IService).loadUserByUsername(loginDto.username());

        if (!this.passwordEncoder.matches(loginDto.password(),user.getPassword())){
            throw new BadCredentialsException("Bad credentials");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        user.getAuthorities()
                )
        );

        // 2. Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT token
        String token = jwtUtils.generateToken(authentication);

        UserTokenResultDto userTokenResultDto = new UserTokenResultDto(
                user.getUsername(),user.getEmail(),user.getRoles(),
               token
        );
        return buildResponseDto(userTokenResultDto);
    }

    public ResponseDto register(UserCreateDto userCreateDto){

        List<String> passwordRequirements = PasswordChecker.evaluate(userCreateDto.password()).missingRequirements();
        if(!passwordRequirements.isEmpty()){
            throw new TaskManagerUserAuthenticationException(
                passwordRequirements.toString()
            );
        }

        UserDto userDto = userCreateDto.updatePassword(
                this.passwordEncoder.encode(userCreateDto.password())
        );
        return this.add(userDto);
    }

}
