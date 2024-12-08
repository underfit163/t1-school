package ru.t1.t1school.mapper;

import org.mapstruct.*;
import ru.t1.t1school.dto.RequestTaskDto;
import ru.t1.t1school.dto.ResponseTaskDto;
import ru.t1.t1school.entity.Task;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
    Task toEntity(RequestTaskDto requestTaskDto);

    ResponseTaskDto toResponseTaskDto(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task partialUpdate(RequestTaskDto requestTaskDto, @MappingTarget Task task);
}