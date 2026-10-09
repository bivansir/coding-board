package ru.coding_board.session_service.task.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;

@Builder
public record TaskResponse(
    @Schema(description = "ID задачи", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID taskId,

    @Schema(description = "Название задачи", example = "Two sum")
    String title,

    @Schema(description = "Описание задачи", example = "...")
    String description,

    @Schema(description = "Уровень сложности", example = "JUNIOR")
    Difficulty difficulty,

    @Schema(description = "Категория задачи", example = "ALGORITHMS")
    Category category,

    @Schema(description = "Язык программирования", example = "JAVA")
    ProgrammingLanguage programmingLanguage,

    @Schema(description = "Видимость задачи", example = "true")
    Boolean isActive
) {
    
}
