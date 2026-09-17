package com.cova.taskmanager.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatus {
    STARTED,
    CREATED,
    IN_PROGRESS,
    CANCEL,
    CLOSED,
    TERMINATED;

    @JsonCreator
    public static TaskStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return CREATED;
        }

        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }

        return CREATED;
    }
}
