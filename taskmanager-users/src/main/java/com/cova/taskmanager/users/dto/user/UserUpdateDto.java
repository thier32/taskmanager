package com.cova.taskmanager.users.dto.user;

public record UserUpdateDto(
        Long userId,
        String title,
        String description,
        String status
) {
}
