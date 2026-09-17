package com.cova.taskmanager.users.services.impl;

import com.cova.taskmanager.users.config.exceptions.TaskManagerUserAuthenticationException;
import com.cova.taskmanager.users.dto.user.UserDto;
import com.cova.taskmanager.users.dto.user.UserResultDto;
import com.cova.taskmanager.users.model.User;
import com.cova.taskmanager.users.repository.UserRepository;
import com.cova.taskmanager.users.services.IUserService;
import com.frame.base.services.BaseService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService extends BaseService<
        UserDto,
        UserResultDto,
        User,
        UserRepository>
        implements IUserService, UserDetailsService
{
    public UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.findSingle(Map.of(User.USER_USERNAME, username));

        if (user == null) {
            user = this.findSingle(Map.of(User.USER_EMAIL, username));
        }

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
    }

    @Override
    public User buildEntity(UserDto entityDto, User entity) {
        User user = this.findSingle(Map.of(User.USER_USERNAME, entityDto.getUsername()));

        if (user == null) {
            user = this.findSingle(Map.of(User.USER_EMAIL, entityDto.getEmail()));
        }

        if (user != null) {
            throw new TaskManagerUserAuthenticationException("User already exists");
        }

        return super.buildEntity(entityDto, entity);
    }
}
