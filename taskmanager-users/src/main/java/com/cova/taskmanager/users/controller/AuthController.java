package com.cova.taskmanager.users.controller;

import com.cova.taskmanager.users.business.UserBusiness;
import com.cova.taskmanager.users.dto.user.UserCreateDto;
import com.cova.taskmanager.users.dto.user.UserLoginDto;
import com.frame.base.business.IBusiness;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.cova.taskmanager.users.constant.SystemRoutes.*;


@RestController
@RequestMapping(USERS_AUTH_ROUTE)
@CrossOrigin(origins = "*")
@Tag(name = USERS_AUTH_NAME)
public class AuthController{

    private final IBusiness business;

    public AuthController(UserBusiness userBusiness)
    {
        this.business = userBusiness;
    }

    @PostMapping(USERS_LOGIN_ROUTE)
    public ResponseEntity $Auth(
            @RequestBody UserLoginDto entityDto) {
        return ResponseEntity.ok(((UserBusiness)this.business).login(entityDto));
    }


    @PostMapping(USERS_REGISTER_ROUTE)
    public ResponseEntity $Register(
            @RequestBody UserCreateDto userCreateDto) {
        return ResponseEntity.ok(((UserBusiness)this.business).register(userCreateDto));
    }
}
