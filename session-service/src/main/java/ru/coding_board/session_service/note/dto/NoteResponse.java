package ru.coding_board.session_service.note.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder 
public record NoteResponse(
    @Schema(description = "ID заметки", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID noteId,

    @Schema(description = "ID сессии", example = "a1a8c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionId,

    @Schema(description = "Заметка во время проведения интервью", example = "Молчит уже 5 минут")
    String note,

    @Schema(description = "Время заметки", example = "2026-03-15T14:00:00")
    LocalDateTime time
) {
    
}
