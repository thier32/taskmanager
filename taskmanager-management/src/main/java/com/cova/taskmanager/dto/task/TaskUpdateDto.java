package com.cova.taskmanager.dto.task;

public record TaskUpdateDto(
        Long taskId,
        String title,
        String description,
        String status
) {
}
