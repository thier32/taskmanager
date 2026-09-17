package com.cova.taskmanager.dto.task;

public record TaskResultDto(
        Long taskId,
        String title,
        String description,
        String status
) {
}
