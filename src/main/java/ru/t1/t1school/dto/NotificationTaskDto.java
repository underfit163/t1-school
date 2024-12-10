package ru.t1.t1school.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.t1.t1school.entity.Status;

/**
 * DTO for {@link ru.t1.t1school.entity.Task}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationTaskDto(@NotNull @Positive Long id, @NotNull Status status) {
}