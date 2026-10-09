package ru.coding_board.session_service.session.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.coding_board.session_service.common.enums.SessionStatus;

public record SessionFilterRequest(
    @Schema(description = "Нижняя граница поиска по дате", example = "2026-03-15T14:00:00") 
    LocalDate startDate,

    @Schema(description = "Верхняя граница поиска по дате", example = "2026-03-15T14:00:00") 
    LocalDate endDate,

    @Schema(description = "Список ID нужных позиций") 
    List<UUID> positions,

    @Schema(description = "Статус сессии") 
    SessionStatus status,

    @Schema(description = "ID интервьюера", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID interviewerId,

    @Schema(description = "ID кандидата", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID candidateId
) {}
