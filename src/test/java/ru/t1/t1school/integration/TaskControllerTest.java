package ru.t1.t1school.integration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.t1school.entity.Status;
import ru.t1.t1school.entity.Task;
import ru.t1.t1school.repository.TaskRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest extends TestContainersConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(1L);
        task.setStatus(Status.OPEN);
        task = taskRepository.save(task);
    }

    @Test
    @DisplayName("Тест получение всех задач")
    void getListTask() throws Exception {
        Task task2 = new Task();
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setUserId(1L);
        task2.setStatus(Status.OPEN);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(taskRepository.count()))
                .andExpect(jsonPath("$[1].title").value("Test Task 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"))
                .andExpect(jsonPath("$[1].userId").value(1L))
                .andExpect(jsonPath("$[1].status").value(Status.OPEN.toString()));
    }

    @Test
    @DisplayName("Тест получение задачи по id")
    void getTask() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.status").value(Status.OPEN.toString()));
    }

    @Test
    @DisplayName("Тест получение задачи по несуществующему id")
    void getTaskNotFound() throws Exception {
        Long taskId = 999L;
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The task with the ID %d does not exist".formatted(taskId)));
    }

    @Test
    @DisplayName("Тест получение задачи по id меньше 0")
    void getTaskByNegativeId() throws Exception {
        Long taskId = -999L;
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Field validation")))
                .andExpect(content().string(Matchers.containsString("must be greater than 0")));
    }

    @Test
    @DisplayName("Тест создание задачи")
    void createTask() throws Exception {
        String requestJson = """
            {
                "title": "Test Task",
                "description": "Test Description",
                "userId": 1,
                "status": "OPEN"
            }
        """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.status").value(Status.OPEN.toString()));
    }

    @Test
    @DisplayName("Тест создание задачи с некорректными статусом")
    void createTaskInvalidStatus() throws Exception {
        String requestJson = """
            {
                "title": "Test",
                "description": "",
                "userId": null,
                "status": "NOTSUPPORTED"
            }
        """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Failed to read request")));
    }

    @Test
    @DisplayName("Тест создание задачи с пустым описанием")
    void createTaskEmptyTitle() throws Exception {
        String requestJson = """
            {
                "title": null,
                "description": "",
                "userId": null,
                "status": "OPEN"
            }
        """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Field validation exception")))
                .andExpect(content().string(Matchers.containsString("must not be blank")));
    }

    @Test
    @DisplayName("Тест обновления задачи")
    void updateTask() throws Exception {
        String updateJson = """
            {
                "title": "Updated Task",
                "description": "Updated Description",
                "userId": 2,
                "status": "INPROGRESS"
            }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.status").value("INPROGRESS"));

    }

    @Test
    @DisplayName("Тест обновления задачи с некорректными данными")
    void updateTaskInvalidData() throws Exception {
        String updateJson = """
            {
                "title": null,
                "description": "",
                "userId": null,
                "status": ""
            }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Failed to read request")));
    }

    @Test
    @DisplayName("Тест обновления несуществующей задачи")
    void updateTaskNotFound() throws Exception {
        String updateJson = """
            {
                "title": "Updated Task",
                "description": "Updated Description",
                "userId": 2,
                "status": "INPROGRESS"
            }
        """;

        Long taskId = 999L;
        mockMvc.perform(put("/api/v1/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The task with the ID %d does not exist".formatted(taskId)));
    }

    @Test
    @DisplayName("Тест удаления задачи")
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{id}", task.getId()))
                .andExpect(status().isOk());

        Assertions.assertFalse(taskRepository.findById(task.getId()).isPresent());
    }

    @Test
    @DisplayName("Тест удаления несуществующей задачи")
    void deleteTaskNotFound() throws Exception {
        Long taskId = 999L;
        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The task with the ID %d does not exist".formatted(taskId)));
    }
}
