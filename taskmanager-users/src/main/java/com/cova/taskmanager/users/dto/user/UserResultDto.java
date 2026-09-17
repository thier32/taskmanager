package com.cova.taskmanager.users.dto.user;

public record UserResultDto(
        Long userId,
        String name,
        String email,
        String status
) {
}
