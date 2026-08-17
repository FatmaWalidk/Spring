package com.fatma.taskmanger.task.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateTaskRequest(
        @NotBlank
        String title,

        String description,

        boolean completed,

        Set<String> tags
) {
}
