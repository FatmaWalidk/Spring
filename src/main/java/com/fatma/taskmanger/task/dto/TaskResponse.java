package com.fatma.taskmanger.task.dto;

import java.util.Set;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        Long ownerId,
        Set<String> tags
) {
}
