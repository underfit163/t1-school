package ru.t1.t1school.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.entity.Status;
import ru.t1.t1school.entity.Task;
import ru.t1.t1school.mapper.TaskMapper;
import ru.t1.t1school.mapper.TaskMapperImpl;
import ru.t1.t1school.repository.TaskRepository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;
    private final TaskMapper taskMapper = new TaskMapperImpl();

    Task task1;
    ResponseTaskDto responseTaskDto;


    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, taskMapper);
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.OPEN);
        task1.setUserId(1L);

        responseTaskDto
                = new ResponseTaskDto(1L, "Task1", "Task1 description", 1L, Status.OPEN);
    }

    @Test
    @DisplayName("Тест поиска всех задач")
    void getAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task1));

        List<ResponseTaskDto> tasks = taskService.getAllTasks();

        assertThat(tasks).hasSize(1);
        assertThat(this.responseTaskDto).isEqualTo(tasks.get(0));
    }

    @Test
    @DisplayName("Тест поиска всех задач, если задач нет")
    void getAllTasks_whenTasksNotFound() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<ResponseTaskDto> tasks = taskService.getAllTasks();

        assertThat(tasks).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("Тест поиска задачи по id")
    @CsvSource({"1"})
    void getTaskById(Long taskId) {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task1));

        ResponseTaskDto responseTaskDtoAfterReturn = taskService.getTaskById(taskId);

        assertThat(responseTaskDtoAfterReturn).isNotNull();
        assertThat(responseTaskDtoAfterReturn).isEqualTo(responseTaskDto);
    }

    @ParameterizedTest
    @DisplayName("Тест поиска задачи по id, если задачи нет")
    @CsvSource({"9999"})
    void getTaskById_whenTaskNotFound(Long notFoundTaskId) {
        when(taskRepository.findById(notFoundTaskId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> taskService.getTaskById(notFoundTaskId))
                .withMessageContaining("The task with the ID %d does not exist", notFoundTaskId);
    }

    @ParameterizedTest
    @DisplayName("Тест поиска задачи по id, проверка валидации входящего параметра")
    @CsvSource({
            "null, не должно равняться null",
            "-1, должно быть больше 0"
    })
    void getTaskById_whenValidationFails(String id, String expectedMessage) throws NoSuchMethodException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Long invalidId = id.equals("null") ? null : Long.valueOf(id);

        Set<ConstraintViolation<TaskServiceImpl>> violations =
                validator.forExecutables().validateParameters(
                        taskService,
                        TaskServiceImpl.class.getMethod("getTaskById", Long.class),
                        new Object[]{invalidId});

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TaskServiceImpl> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Тест создания задачи")
    void createTask() {
        RequestTaskDto createTaskDto
                = new RequestTaskDto(task1.getTitle(), task1.getDescription(), task1.getUserId(), task1.getStatus());

        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        ResponseTaskDto responseTaskDtoAfterReturn = taskService.createTask(createTaskDto);

        assertThat(responseTaskDtoAfterReturn).isNotNull();
        assertThat(responseTaskDtoAfterReturn).isEqualTo(responseTaskDto);
    }

    @ParameterizedTest
    @DisplayName("Тест обновления задач")
    @CsvSource({"1"})
    void updateTask(Long taskId) {
        RequestTaskDto updateTaskDto = new RequestTaskDto("New task1", "New task1 description", 2L, Status.CLOSED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task1));

        when(taskRepository.save(any(Task.class))).thenReturn(taskMapper.partialUpdate(updateTaskDto, task1));

        ResponseTaskDto responseTaskDtoAfterReturn = taskService.updateTask(taskId, updateTaskDto);

        assertThat(responseTaskDtoAfterReturn).isNotNull();
        assertThat(responseTaskDtoAfterReturn.id()).isEqualTo(taskId);
        assertThat(responseTaskDtoAfterReturn.title()).isEqualTo(updateTaskDto.title());
        assertThat(responseTaskDtoAfterReturn.description()).isEqualTo(updateTaskDto.description());
        assertThat(responseTaskDtoAfterReturn.userId()).isEqualTo(updateTaskDto.userId());
        assertThat(responseTaskDtoAfterReturn.status()).isEqualTo(updateTaskDto.status());
    }

    @ParameterizedTest
    @DisplayName("Тест удаления задачи")
    @CsvSource({"1"})
    void deleteTask(Long taskId) {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        taskService.deleteTask(taskId);

        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @ParameterizedTest
    @DisplayName("Тест удаления задачи, если задачи нет")
    @CsvSource({"9999"})
    void deleteTask_whenTaskNotFound(Long notFoundTaskId) {
        when(taskRepository.existsById(notFoundTaskId)).thenReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> taskService.deleteTask(notFoundTaskId))
                .withMessageContaining("The task with the ID %d does not exist", notFoundTaskId);
    }
}