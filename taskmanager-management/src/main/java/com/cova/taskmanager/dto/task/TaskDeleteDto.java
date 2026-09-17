package com.cova.taskmanager.dto.task;

import java.util.List;

public record TaskDeleteDto(
        List<Long> taskId
) {
}
