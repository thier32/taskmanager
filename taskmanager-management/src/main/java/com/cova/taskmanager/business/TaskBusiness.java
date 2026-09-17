package com.cova.taskmanager.business;

import com.cova.taskmanager.dto.task.TaskCreateDto;
import com.cova.taskmanager.dto.task.TaskDto;
import com.cova.taskmanager.dto.task.TaskResultDto;
import com.cova.taskmanager.services.impl.TaskService;
import com.frame.base.business.BaseBusiness;
import org.springframework.stereotype.Service;

@Service
public class TaskBusiness extends BaseBusiness<TaskResultDto, TaskCreateDto> {

    public TaskBusiness(TaskService taskService)
    {
        super(taskService);
    }
}
