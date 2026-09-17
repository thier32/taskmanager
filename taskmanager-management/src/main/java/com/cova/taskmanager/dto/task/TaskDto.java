package com.cova.taskmanager.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TaskDto {
    Long taskId;
    String title;
    String description;
    String status;
    Date createdAt;
    Date updatedAt;

    public TaskDto(Long newId, String title, String description, String status) {
        this.taskId = newId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = null;
        this.updatedAt = null;
    }
}

