package ru.coding_board.session_service.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CandidateFilterRequest(
    @Schema(description = "ФИО кандидата", example = "Иванов Иван Иванович")
    String fullName,

    @Schema(description = "Видимость", example = "true")
    Boolean isActive
) {}
