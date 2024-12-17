package ru.t1.t1school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.logstarter.aspect.annotation.HttpLogExecution;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.service.TaskNotificationFacade;
import ru.t1.t1school.service.TaskService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskNotificationFacade taskNotificationFacade;

    @HttpLogExecution
    @GetMapping
    public List<ResponseTaskDto> getListTask() {
        return taskService.getAllTasks();
    }

    @HttpLogExecution
    @GetMapping("/{id}")
    public ResponseTaskDto getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @HttpLogExecution
    @PostMapping
    public ResponseTaskDto createTask(@RequestBody RequestTaskDto requestTaskDto) {
        return taskService.createTask(requestTaskDto);
    }

    @HttpLogExecution
    @PutMapping("/{id}")
    public ResponseTaskDto updateTask(@PathVariable Long id, @RequestBody RequestTaskDto requestTaskDto) {
        return taskNotificationFacade.updateTaskWithNotification(id, requestTaskDto);
    }

    @HttpLogExecution
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
