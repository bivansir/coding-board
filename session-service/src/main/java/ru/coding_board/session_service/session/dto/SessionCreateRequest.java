package ru.coding_board.session_service.session.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


public record SessionCreateRequest(
    @NotNull(message = "positionId не может быть NULL")
    @Schema(description = "ID позиции собеседования", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID positionId,

    @NotNull(message = "candidateId не может быть NULL")
    @Schema(description = "ID кандидата на позицию", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID candidateId,

    @NotNull(message = "interviewerId не может быть NULL")
    @Schema(description = "ID интервьюера", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID interviewerId,

    @NotNull(message = "scheduledAt не может быть NULL")
    @Schema(description = "Запланированное начало сессии", example = "2026-03-15T14:00:00")
    LocalDateTime scheduledAt
) {}
