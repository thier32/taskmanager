package com.cova.taskmanager.model;

import com.cova.taskmanager.users.model.TaskStatus;
import com.frame.base.model.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "tasks")
public class Task extends BaseModel {
        public static final String TASK_ID = "taskId";
        public static final String TASK_NAME = "name";
        public static final String TASK_TITLE = "title";
        public static final String TASK_DESCRIPTION = "description";
        public static final String TASK_STATUS = "status";
        // 1. Les Attributs
        private Long taskId;
        private String name;
        private String title;
        private String description;

        @Enumerated(EnumType.STRING)
        private com.cova.taskmanager.users.model.TaskStatus status = TaskStatus.CREATED;

        @ManyToOne(fetch = FetchType.LAZY) // Optimisé en LAZY
        @JoinColumn(name = "parentId")
        private Task parent;

        // Les Fils (Optionnel, pour la vue descendante)
        @OneToMany(mappedBy = "parent")
        @Fetch(FetchMode.SUBSELECT) // <--- Évite le N+1 sur l'arborescence
        private Set<Task> children = new LinkedHashSet<>(10);

        public Task() {}


        public Task addTask(Task task) {
                children.add(task);
                return this;
        }

        public Task removeTask(Task task) {
                children.remove(task);
                return this;
        }

        @Override
        public boolean equals(Object obj) {
                return Objects.equals(this.taskId, ((Task) obj).getTaskId());
        }
}
