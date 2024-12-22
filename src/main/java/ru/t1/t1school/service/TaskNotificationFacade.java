package ru.t1.t1school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.t1.logstarter.aspect.annotation.LogExecution;
import ru.t1.t1school.dto.NotificationTaskDto;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.entity.Status;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskNotificationFacade {
    private final TaskService taskService;
    private final KafkaProducerService kafkaProducerService;

    @LogExecution
    public ResponseTaskDto updateTaskWithNotification(Long id, RequestTaskDto requestTaskDto) {
        ResponseTaskDto beforeUpdateTask = taskService.getTaskById(id);
        Status oldStatus = beforeUpdateTask.status();
        //Обновление задачи
        ResponseTaskDto updatedTask = taskService.updateTask(id, requestTaskDto);

        // Проверка на изменение статуса
        if (!Objects.equals(oldStatus, updatedTask.status())) {
            NotificationTaskDto notificationTaskDto = new NotificationTaskDto(id, requestTaskDto.status());
            kafkaProducerService.sendStatusTaskUpdate(notificationTaskDto);
        }

        return updatedTask;
    }
}
