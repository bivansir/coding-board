package ru.coding_board.session_service.task.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.common.util.StringStripper;

public record TaskCreateRequest(
    @NotBlank(message = "Поле title не может быть BLANK")
    @Schema(description = "Название задачи", example = "Two sum")
    String title,

    @NotBlank(message = "Поле description не может быть BLANK")
    @Schema(description = "Описание задачи", example = "...")
    String description,

    @NotNull(message = "Поле difficulty не может быть NULL")
    @Schema(description = "Уровень сложности", example = "JUNIOR")
    Difficulty difficulty,

    @NotNull(message = "Поле category не может быть NULL")
    @Schema(description = "Категория задачи", example = "ALGORITHMS")
    Category category,

    ProgrammingLanguage programmingLanguage
) {
    public TaskCreateRequest {
        title = StringStripper.stripOrNull(title);
    }
}
