package com.cova.taskmanager.users.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

@NoArgsConstructor
public class UserDto {
    Long userId;
    String username;
    String password;
    String email;
    String name;

    List<String> roles;
    public UserDto(String username, String password, String email,List<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }
    public UserDto(Long userId, String username, String password, String email,List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public UserDto(String username, String password, String email, String name, List<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.name=name;
    }
}

