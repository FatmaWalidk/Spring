package com.fatma.taskmanger.task;

import com.fatma.taskmanger.task.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "ownerId", source = "user.id")
    @Mapping(target = "tags", expression = "java(mapTagNames(task))")
    TaskResponse toResponse(Task task);

    default Set<String> mapTagNames(Task task) {
        return task.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }
}
