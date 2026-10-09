package ru.coding_board.session_service.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.coding_board.session_service.common.util.StringStripper;

public record CandidatePatchRequest(
    @Schema(description = "Имя кандидата", example = "Иван")
    String name,

    @Schema(description = "Фамилия кандидата", example = "Иванов")
    String surname,

    @Schema(description = "Отчество кандидата", example = "Иванович")
    String patronymic,

    @Schema(description = "Email кандидата", example = "test@gmail.com")
    String email,

    @Schema(description = "Номер телефона кандидата", example = "+79113878490")
    String phoneNumber,

    @Schema(description = "HH-id резюме кандидата", example = "124146")
    String resumeId,

    @Schema(description = "Видимость кандидата", example = "true")
    Boolean isActive
) {
    public CandidatePatchRequest {
        email = StringStripper.stripOrNull(email);
        phoneNumber = StringStripper.stripOrNull(phoneNumber);
        resumeId = StringStripper.stripOrNull(resumeId);
    }
}
