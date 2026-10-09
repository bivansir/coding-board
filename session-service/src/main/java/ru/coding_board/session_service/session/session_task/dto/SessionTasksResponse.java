package ru.coding_board.session_service.session.session_task.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
public record SessionTasksResponse(
    @Schema(description = "ID SessionTask", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionId,

    @Schema(description = "Список вложенных View SessionTask")
    List<SessionTaskResponse> sessionTasks
) {
    
}
