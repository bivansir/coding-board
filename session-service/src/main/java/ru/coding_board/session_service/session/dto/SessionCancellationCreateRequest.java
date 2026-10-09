package ru.coding_board.session_service.session.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessionCancellationCreateRequest(

    @NotBlank(message = "Поле cancellationReason не может быть BLANK")
    @Schema(description = "Причина отмены сессии", example = "Кандидат отказался от интервью")
    String cancellationReason,

    @NotNull(message = "Поле hrId не может быть NULL")
    @Schema(description = "ID отменившего сессию hr", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID hrId,

    @Schema(description = "Время отказа", example = "2026-03-15T14:00:00")
    LocalDateTime cancellationTime
) {
    
}
