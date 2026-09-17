package com.cova.taskmanager.users.dto.user;

public record UserLoginDto(
        String username,
        String email,
        String password
) {
}
