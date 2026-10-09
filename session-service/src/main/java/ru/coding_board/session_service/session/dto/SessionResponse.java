package ru.coding_board.session_service.session.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.position.dto.PositionResponse;


@Builder(toBuilder = true)
public record SessionResponse(
    @Schema(description = "ID сессии", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionId,

    @Schema(description = "Статус сессии")
    SessionStatus status,

    @Schema(description = "Вложенный View Позиции")
    PositionResponse positionResponse,

    @Schema(description = "ID кандидата", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID candidateId,
    
    @Schema(description = "ID интервьюера", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID interviewerId,

    @Schema(description = "Токен доступа к сессии", example = "dGhpcyBpcyBhIHNhbXBsZSB0b2tlbiBmb3IgZGVtb25zdHJhdGlvbiE")
    String inviteToken,

    @Schema(description = "Запланированная дата сессии", example = "2026-03-15T14:00:00") 
    LocalDateTime scheduledAt,

    @Schema(description = "Дата окончания сессии", example = "2026-03-15T14:00:00") 
    LocalDateTime completedAt,

    @Schema(description = "Фактическое время начала", example = "2026-03-15T14:00:00") 
    LocalDateTime startedAt,

    @Schema(description = "View отмены сессии") 
    SessionCancellationResponse sessionCancellation
) {}