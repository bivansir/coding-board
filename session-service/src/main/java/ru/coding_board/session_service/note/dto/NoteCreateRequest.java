package ru.coding_board.session_service.note.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record NoteCreateRequest(
    @NotNull
    @Schema(description = "Заметка во время проведения интервью", example = "Молчит уже 5 минут")
    String note,

    @NotNull(message = "Поле time не может быть NULL")
    @Schema(description = "Время заметки", example = "2026-03-15T14:00:00")
    LocalDateTime time
) {}
