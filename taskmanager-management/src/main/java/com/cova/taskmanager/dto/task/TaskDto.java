package com.cova.taskmanager.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDto {
    Long taskId;
    String title;
    String description;
    String status;
}

