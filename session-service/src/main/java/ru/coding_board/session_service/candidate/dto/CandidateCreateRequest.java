package ru.coding_board.session_service.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import ru.coding_board.session_service.common.util.StringStripper;

public record CandidateCreateRequest(
    @NotBlank(message = "Поле name не может быть BLANK")
    @Schema(description = "Имя кандидата", example = "Иван")
    String name,

    @NotBlank(message = "Поле surname не может быть BLANK")
    @Schema(description = "Фамилия кандидата", example = "Иванов")
    String surname,

    @Schema(description = "Отчество кандидата", example = "Иванович")
    String patronymic,

    @NotBlank(message = "Поле email не может быть BLANK")
    @Schema(description = "Email кандидата", example = "test@gmail.com")
    String email,

    @Schema(description = "Номер телефона кандидата", example = "+79113878490")
    String phoneNumber,

    @Schema(description = "HH-id резюме кандидата", example = "124146")
    String resumeId
) {
    public CandidateCreateRequest {
        email = StringStripper.stripOrNull(email);
        phoneNumber = StringStripper.stripOrNull(phoneNumber);
        resumeId = StringStripper.stripOrNull(resumeId);
    }
}
