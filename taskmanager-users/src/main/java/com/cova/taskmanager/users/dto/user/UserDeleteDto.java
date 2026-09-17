package com.cova.taskmanager.users.dto.user;

import java.util.List;

public record UserDeleteDto(
        List<Long> userId
) {
}
