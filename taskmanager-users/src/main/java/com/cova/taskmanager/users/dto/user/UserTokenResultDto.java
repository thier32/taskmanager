package com.cova.taskmanager.users.dto.user;

import java.util.List;

public record UserTokenResultDto(
        String username,
        String email,
        List<String> roles,
        String bearer
) {
}
