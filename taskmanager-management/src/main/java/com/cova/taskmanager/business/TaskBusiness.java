package com.cova.taskmanager.business;

import com.cova.taskmanager.dto.task.TaskCreateDto;
import com.cova.taskmanager.dto.task.TaskDto;
import com.cova.taskmanager.dto.task.TaskResultDto;
import com.cova.taskmanager.services.impl.TaskService;
import com.cova.taskmanager.users.dto.user.UserLoginDto;
import com.cova.taskmanager.users.model.TaskStatus;
import com.frame.base.business.BaseBusiness;
import com.frame.base.dto.ResponseDto;
import com.frame.base.model.BaseModel;
import com.frame.base.security.RuntimeSecurityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TaskBusiness extends BaseBusiness<TaskResultDto, TaskCreateDto> {

    public TaskBusiness(TaskService taskService) {
        super(taskService);
    }

    public ResponseDto getStatus() {
        return buildResponseDto(TaskStatus.values());
    }

    public ResponseDto findAllData(Map<?, ?> map) {
        Map<Object, Object> inpuMap = new HashMap<>(map != null ? map : Map.of());
        inpuMap.put(BaseModel.CREATED_BY_USERNAME, RuntimeSecurityUtils.getCurrentUsernameSafely());
        return super.findAllData(inpuMap);
    }
}