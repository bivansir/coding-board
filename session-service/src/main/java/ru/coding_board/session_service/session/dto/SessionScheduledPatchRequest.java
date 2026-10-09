package ru.coding_board.session_service.session.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record SessionScheduledPatchRequest(
    @Schema(description = "ID позиции", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID positionID,

    @Schema(description = "ID кандидата", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID candidateID,

    @Schema(description = "ID интервьюера", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID interviewerID,

    @Schema(description = "Запланированная дата сессии", example = "2026-03-15T14:00:00") 
    LocalDateTime scheduledAt
) { }
