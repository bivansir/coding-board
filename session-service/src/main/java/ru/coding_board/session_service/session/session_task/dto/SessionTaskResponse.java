package ru.coding_board.session_service.session.session_task.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.task.dto.TaskResponse;


@Builder
public record SessionTaskResponse(
    @Schema(description = "ID SessionTask", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionTaskId,

    @Schema(description = "Вложенный View Task")
    TaskResponse task,

    @Schema(description = "Язык программирования", example = "Java")
    ProgrammingLanguage programmingLanguage
) {
    
}
