package ru.coding_board.session_service.session.session_task.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;

public record SessionTasksPatchRequest(
    @NotNull
    @Schema(description = "ID SessionTask", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionTaskId,
    
    @Schema(description = "Язык программирования", example = "Java")
    ProgrammingLanguage programmingLanguage
) {
    
}
