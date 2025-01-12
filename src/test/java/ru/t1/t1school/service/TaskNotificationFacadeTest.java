package ru.t1.t1school.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.t1school.dto.NotificationTaskDto;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.entity.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskNotificationFacadeTest {
    @InjectMocks
    private TaskNotificationFacade taskNotificationFacade;

    @Mock
    private TaskService taskService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @ParameterizedTest
    @DisplayName("Тест обновления статуса задачи, если статус изменился")
    @CsvSource({"1"})
    void updateTaskWithNotification(Long taskId) {
        RequestTaskDto requestTaskDto
                = new RequestTaskDto("Updated title", "Updated description", 1L, Status.INPROGRESS);
        ResponseTaskDto beforeUpdateTask
                = new ResponseTaskDto(taskId, "title", "description", 1L, Status.OPEN);
        ResponseTaskDto updatedTask
                = new ResponseTaskDto(taskId, "Updated title", "Updated description", 1L, Status.INPROGRESS);

        when(taskService.getTaskById(taskId)).thenReturn(beforeUpdateTask);
        when(taskService.updateTask(taskId, requestTaskDto)).thenReturn(updatedTask);

        ResponseTaskDto result = taskNotificationFacade.updateTaskWithNotification(taskId, requestTaskDto);

        assertEquals(updatedTask, result);
        verify(kafkaProducerService).sendStatusTaskUpdate(new NotificationTaskDto(taskId, requestTaskDto.status()));
    }

    @ParameterizedTest
    @DisplayName("Тест обновления статуса задачи, если статус не изменился")
    @CsvSource({"1"})
    void updateTaskWithNotification_shouldNotSendNotification_whenStatusDoesNotChange(Long taskId) {
        RequestTaskDto requestTaskDto
                = new RequestTaskDto("Updated title", "Updated description", 1L, Status.OPEN);
        ResponseTaskDto beforeUpdateTask
                = new ResponseTaskDto(taskId, "title", "description", 1L, Status.OPEN);
        ResponseTaskDto updatedTask
                = new ResponseTaskDto(taskId, "Updated title", "Updated description", 1L, Status.OPEN);

        when(taskService.getTaskById(taskId)).thenReturn(beforeUpdateTask);
        when(taskService.updateTask(taskId, requestTaskDto)).thenReturn(updatedTask);

        ResponseTaskDto result = taskNotificationFacade.updateTaskWithNotification(taskId, requestTaskDto);

        assertEquals(updatedTask, result);
        verify(kafkaProducerService, never()).sendStatusTaskUpdate(any());
    }
}