package com.cova.taskmanager.users.dto.user;

public record UserDeniedResultDto(
        String message,
        int code
) {
}
