package com.cova.taskmanager.users.controller;

import com.cova.taskmanager.users.business.UserBusiness;
import com.cova.taskmanager.users.constant.SystemRoutes;
import com.cova.taskmanager.users.dto.user.UserCreateDto;
import com.cova.taskmanager.users.dto.user.UserLoginDto;
import com.cova.taskmanager.users.dto.user.UserResultDto;
import com.frame.base.controller.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.cova.taskmanager.users.constant.SystemRoutes.*;


@RestController
@RequestMapping(SystemRoutes.USERS_ROUTE)
@CrossOrigin(origins = "*")
@Tag(name = USERS_ROUTE_NAME)
public class UserController extends BaseController<UserResultDto, UserCreateDto>
{
    public UserController(UserBusiness userBusiness)
    {
        this.business = userBusiness;
    }
}
