package ru.t1.t1school.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.t1.t1school.aspect.annotation.LogExecution;
import ru.t1.t1school.aspect.annotation.TimeExecutionTracking;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.entity.Task;
import ru.t1.t1school.mapper.TaskMapper;
import ru.t1.t1school.repository.TaskRepository;

import java.util.List;

@Validated
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @LogExecution
    @TimeExecutionTracking
    public List<ResponseTaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::toResponseTaskDto).toList();
    }

    @Override
    @LogExecution
    @TimeExecutionTracking
    @Cacheable(value = "task", key = "#id")
    public ResponseTaskDto getTaskById(@NotNull @Positive Long id) {
        return taskMapper.toResponseTaskDto(getTaskFromRepository(id));
    }

    @Override
    @Transactional
    @LogExecution
    @TimeExecutionTracking
    @CachePut(value = "task", key = "#result.id()")
    public ResponseTaskDto createTask(@Valid RequestTaskDto requestTaskDto) {
        return taskMapper.toResponseTaskDto(taskRepository.save(taskMapper.toEntity(requestTaskDto)));
    }

    @Transactional
    @Override
    @LogExecution
    @TimeExecutionTracking
    @CachePut(value = "task",key = "#id")
    public ResponseTaskDto updateTask(@NotNull @Positive Long id, @Valid RequestTaskDto requestTaskDto) {
        Task task = getTaskFromRepository(id);
        task = taskMapper.partialUpdate(requestTaskDto, task);
        return taskMapper.toResponseTaskDto(taskRepository.save(task));
    }

    @Transactional
    @Override
    @LogExecution
    @TimeExecutionTracking
    @CacheEvict(value = "task",key = "#id")
    public void deleteTask(@NotNull @Positive Long id) {
        if (!taskRepository.existsById(id))
            throw new EntityNotFoundException("The task with the ID %s does not exist".formatted(id));
        taskRepository.deleteById(id);
    }

    private Task getTaskFromRepository(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The task with the ID %s does not exist".formatted(id)));
    }
}
