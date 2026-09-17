package com.cova.taskmanager.users.repository;

import com.cova.taskmanager.users.model.User;
import com.frame.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, Long>
{
}
