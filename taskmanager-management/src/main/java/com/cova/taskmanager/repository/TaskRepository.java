package com.cova.taskmanager.repository;

import com.cova.taskmanager.model.Task;
import com.frame.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends BaseRepository<Task, Long>
{
}
