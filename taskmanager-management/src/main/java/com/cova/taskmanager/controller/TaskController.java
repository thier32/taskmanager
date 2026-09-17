package com.cova.taskmanager.controller;

import com.cova.taskmanager.business.TaskBusiness;
import com.cova.taskmanager.users.business.UserBusiness;
import com.cova.taskmanager.users.constant.SystemRoutes;
import com.cova.taskmanager.dto.task.TaskCreateDto;
import com.cova.taskmanager.dto.task.TaskResultDto;
import com.cova.taskmanager.users.dto.user.UserLoginDto;
import com.frame.base.controller.BaseController;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.cova.taskmanager.constant.SystemRoutes.*;


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

    @GetMapping(TASK_STATUS_ROUTE)
    public ResponseEntity $Status() {
        return ResponseEntity.ok(((TaskBusiness)this.business).getStatus());
    }
}
