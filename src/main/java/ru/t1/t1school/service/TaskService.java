package ru.t1.t1school.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;

import java.util.List;

public interface TaskService {
    /**
     * Получение списка всех задач
     *
     * @return все задачи
     */
    List<ResponseTaskDto> getAllTasks();

    /**
     * Получение задачи по id
     *
     * @param id задачи
     * @return задача
     */
    ResponseTaskDto getTaskById(@NotNull @Positive Long id);

    /**
     * Создание новой задачи
     *
     * @param requestTaskDto параметры задачи
     * @return созданная задача
     */
    ResponseTaskDto createTask(@Valid RequestTaskDto requestTaskDto);

    /**
     * Обновление задачи
     *
     * @param id             задачи для обновления
     * @param requestTaskDto параметры задачи для обновления
     * @return обновленная задача
     */
    ResponseTaskDto updateTask(@NotNull @Positive Long id, @Valid RequestTaskDto requestTaskDto);

    /**
     * Удаление задачи
     *
     * @param id задачи для удаления
     */
    void deleteTask(@NotNull @Positive Long id);
}
