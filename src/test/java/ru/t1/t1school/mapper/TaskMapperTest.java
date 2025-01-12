package ru.t1.t1school.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.entity.Status;
import ru.t1.t1school.entity.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {
    private final TaskMapper taskMapper = new TaskMapperImpl();

    Task task1;
    RequestTaskDto requestTaskDto;
    ResponseTaskDto responseTaskDto;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.OPEN);
        task1.setUserId(1L);

        requestTaskDto
                = new RequestTaskDto("Task1", "Task1 description", 1L, Status.OPEN);
        responseTaskDto
                = new ResponseTaskDto(1L, "Task1", "Task1 description", 1L, Status.OPEN);
    }

    @Test
    void toEntity() {
        Task taskAfterMapping = taskMapper.toEntity(requestTaskDto);
        taskAfterMapping.setId(1L);

        assertNotNull(taskAfterMapping);
        assertEquals(task1, taskAfterMapping);
    }

    @Test
    void toResponseTaskDto() {
        ResponseTaskDto responseTaskDtoAfterMapping = taskMapper.toResponseTaskDto(task1);

        assertNotNull(responseTaskDtoAfterMapping);
        assertEquals(responseTaskDto, responseTaskDtoAfterMapping);
    }

    @Test
    void partialUpdate() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(Status.CLOSED);

        task = taskMapper.partialUpdate(requestTaskDto, task);

        assertNotNull(task);
        assertEquals(task1, task);
    }

    @Test
    void partialUpdate_ignoreNullValues() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task1");
        task.setDescription("Task1 description");
        task.setStatus(Status.OPEN);
        task.setUserId(1L);

        RequestTaskDto requestTaskDto = new RequestTaskDto(null, null, null, null);

        task = taskMapper.partialUpdate(requestTaskDto, task);

        assertNotNull(task);
        assertEquals(task1, task);
    }
}