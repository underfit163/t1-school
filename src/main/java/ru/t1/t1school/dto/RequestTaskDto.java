package ru.t1.t1school.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link ru.t1.t1school.entity.Task}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RequestTaskDto(@NotBlank String title, String description, Long userId) {
}