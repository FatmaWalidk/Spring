package com.fatma.taskmanger.task.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CreateTaskRequest(
        @NotBlank
        String title,

        String description,

        Set<String> tags
) {
}
