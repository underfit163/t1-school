package ru.t1.t1school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public List<ResponseTaskDto> getListTask() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseTaskDto getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseTaskDto createTask(@RequestBody RequestTaskDto requestTaskDto) {
        return taskService.createTask(requestTaskDto);
    }

    @PutMapping("/{id}")
    public ResponseTaskDto updateTask(@PathVariable Long id, @RequestBody RequestTaskDto requestTaskDto) {
        return taskNotificationFacade.updateTaskWithNotification(id, requestTaskDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
