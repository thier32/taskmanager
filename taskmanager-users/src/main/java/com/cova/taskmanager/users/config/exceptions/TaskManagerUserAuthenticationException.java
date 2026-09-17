package com.cova.taskmanager.users.config.exceptions;


import org.springframework.security.core.AuthenticationException;

public class TaskManagerUserAuthenticationException extends AuthenticationException {
    public TaskManagerUserAuthenticationException(String message){
        super(message);
    }
}
