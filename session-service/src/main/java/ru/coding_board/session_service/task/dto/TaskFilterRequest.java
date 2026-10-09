package ru.coding_board.session_service.task.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.coding_board.session_service.common.enums.Category;
import ru.coding_board.session_service.common.enums.Difficulty;
import ru.coding_board.session_service.common.enums.ProgrammingLanguage;

public record TaskFilterRequest (
    @Schema(description = "Поисковой запрос по названию", example = "Two sum")
    String searchQuery,

    @Schema(description = "Список сложностей для фильтрации")
    List<Difficulty> difficulties,

    @Schema(description = "Категория задачи", example = "ALGORITHMS")
    Category category,

    @Schema(description = "Язык программирования", example = "JAVA")
    ProgrammingLanguage programmingLanguage,

    @Schema(description = "Список ID уже выбранных задач (отфильтровываются)")
    List<UUID> filteredTaskIds
) {}
