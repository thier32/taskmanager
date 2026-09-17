package com.cova.taskmanager.users.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    Long userId;
    String username;
    String password;
    String email;
}

