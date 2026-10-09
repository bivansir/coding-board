package ru.coding_board.session_service.session.session_task.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record SessionTasksCreateRequest(
    @NotEmpty(message = "Поле sessionTasks не может быть NULL")
    @Schema(description = "Список вложенных View SessionTask")
    List<SessionTaskResponse> sessionTasks
) {}
