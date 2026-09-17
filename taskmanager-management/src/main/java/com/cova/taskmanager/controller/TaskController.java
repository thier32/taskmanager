package com.cova.taskmanager.controller;

import com.cova.taskmanager.business.TaskBusiness;
import com.cova.taskmanager.users.constant.SystemRoutes;
import com.cova.taskmanager.dto.task.TaskCreateDto;
import com.cova.taskmanager.dto.task.TaskResultDto;
import com.frame.base.controller.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cova.taskmanager.constant.SystemRoutes.TASKS_ROUTE_NAME;
import static com.cova.taskmanager.constant.SystemRoutes.TASK_ROUTE;


@RestController
@RequestMapping(TASK_ROUTE)
@CrossOrigin(origins = "*")
@Tag(name = TASKS_ROUTE_NAME)
public class TaskController extends BaseController<TaskResultDto, TaskCreateDto>
{
    public TaskController(TaskBusiness taskBusiness)
    {
        this.business = taskBusiness;
    }
}
