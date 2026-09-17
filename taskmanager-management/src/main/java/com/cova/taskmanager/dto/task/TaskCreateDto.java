package com.cova.taskmanager.dto.task;

public record TaskCreateDto(
        String title,
        String description,
        String status
) {
    public TaskDto withId(Long newId) {
        return new TaskDto(newId, this.title, this.description, this.status);
    }
}
