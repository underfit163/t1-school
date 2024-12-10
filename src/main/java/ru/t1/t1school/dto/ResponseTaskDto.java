package ru.t1.t1school.dto;

import ru.t1.t1school.entity.Status;

/**
 * DTO for {@link ru.t1.t1school.entity.Task}
 */
public record ResponseTaskDto(Long id, String title, String description, Long userId, Status status) {
}