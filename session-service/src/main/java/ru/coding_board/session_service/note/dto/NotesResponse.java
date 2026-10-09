package ru.coding_board.session_service.note.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder 
public record NotesResponse(
    @Schema(description = "ID сессии", example = "a1a8c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionId,

    @Schema(description = "Список Note View's для списка")
    List<NoteListItemResponse> notes
) {
    
}
