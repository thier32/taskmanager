package com.cova.taskmanager.users.dto.user;

import java.util.List;

public record UserCreateDto(
        String username,
        String password,
        String confirmPassword,
        String email,
        String name,
        List<String> roles
) {
    public UserDto withId(Long newId) {
        return new UserDto(newId, this.username, this.password, this.email,this.roles);
    }
    public UserDto updatePassword(String newPassword) {
        return new UserDto(this.username, newPassword, this.email, this.name,this.roles);
    }
}
