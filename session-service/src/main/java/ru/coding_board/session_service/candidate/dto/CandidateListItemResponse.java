package ru.coding_board.session_service.candidate.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record CandidateListItemResponse(
        @Schema(description = "ID кандидата", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        UUID candidateId,

        @Schema(description = "Имя кандидата", example = "Иван")
        String name,

        @Schema(description = "Фамилия кандидата", example = "Иванов")
        String surname,

        @Schema(description = "Отчество кандидата", example = "Иванович")
        String patronymic,

        @Schema(description = "Почта", example = "test@gmail.com")
        String email
) {
}
