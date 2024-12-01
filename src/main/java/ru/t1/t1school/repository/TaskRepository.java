package ru.t1.t1school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.t1school.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}