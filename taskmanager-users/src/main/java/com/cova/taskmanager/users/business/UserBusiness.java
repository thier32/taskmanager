package com.cova.taskmanager.users.business;

import com.cova.taskmanager.users.config.JwtUtils;
import com.cova.taskmanager.users.dto.user.UserCreateDto;
import com.cova.taskmanager.users.dto.user.UserLoginDto;
import com.cova.taskmanager.users.dto.user.UserResultDto;
import com.cova.taskmanager.users.dto.user.UserTokenResultDto;
import com.cova.taskmanager.users.services.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frame.base.business.BaseBusiness;
import com.frame.base.dto.ResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
public class UserBusiness extends BaseBusiness<UserResultDto, UserCreateDto> {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public UserBusiness(UserService userService, ObjectMapper objectMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder)
    {
        super(userService);
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseDto login(UserLoginDto loginDto){
        Map<String, Object> map = objectMapper.convertValue(loginDto, Map.class);
        UserResultDto userResultDto = (UserResultDto) this.IService.findDetail(map);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userResultDto.name(),
                        loginDto.password()
                )
        );

        // 2. Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT token
        String token = jwtUtils.generateToken(authentication);

        UserTokenResultDto userTokenResultDto = new UserTokenResultDto(
                userResultDto.name(),userResultDto.email(),new ArrayList<>(10),
               token
        );
        return buildResponseDto(userTokenResultDto);
    }

    public ResponseDto register(UserCreateDto userCreateDto){
        userCreateDto = userCreateDto.updatePassword(
                this.passwordEncoder.encode(userCreateDto.password())
        );
        return this.add(userCreateDto);
    }

}
