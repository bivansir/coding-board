package ru.coding_board.session_service.task.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.common.util.StringStripper;

public record TaskPatchRequest(
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
    public TaskPatchRequest {
        title = StringStripper.stripOrNull(title);
    }
}
