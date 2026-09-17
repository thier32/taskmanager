package com.cova.taskmanager.services.impl;

import com.cova.taskmanager.dto.task.TaskDto;
import com.cova.taskmanager.dto.task.TaskResultDto;
import com.cova.taskmanager.model.Task;
import com.cova.taskmanager.repository.TaskRepository;
import com.cova.taskmanager.services.ITaskService;
import com.frame.base.services.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends BaseService<
        TaskDto,
        TaskResultDto,
        Task,
        TaskRepository>
        implements ITaskService
{
    public TaskService(TaskRepository repository) {
        super(repository);
    }
}
