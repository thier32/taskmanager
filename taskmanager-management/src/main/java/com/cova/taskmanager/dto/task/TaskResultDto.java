package com.cova.taskmanager.dto.task;

import java.util.Date;

public record TaskResultDto(
        Long taskId,
        String title,
        String description,
        String status,
        Date createdAt,
        Date updatedAt
) {
}
