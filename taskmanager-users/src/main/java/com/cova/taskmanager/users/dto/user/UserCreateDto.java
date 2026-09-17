package com.cova.taskmanager.users.dto.user;

public record UserCreateDto(
        String username,
        String password,
        String email,
        String name
) {
    public UserDto withId(Long newId) {
        return new UserDto(newId, this.username, this.password, this.email);
    }
    public UserCreateDto updatePassword(String newPassword) {
        return new UserCreateDto(this.username, newPassword, this.email, this.name);
    }
}
