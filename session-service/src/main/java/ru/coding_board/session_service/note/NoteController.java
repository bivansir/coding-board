package ru.coding_board.session_service.note;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.annotation.CommonErrorResponses;
import ru.coding_board.session_service.common.exception.ErrorResponse;
import ru.coding_board.session_service.note.dto.NoteCreateRequest;
import ru.coding_board.session_service.note.dto.NoteResponse;
import ru.coding_board.session_service.note.dto.NotesResponse;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Note API", description = "API для работы с сущностью Note")
public class NoteController {
    private final NoteService noteService;

    @GetMapping("/{id}/note")
    @Operation(
            summary = "Получить стандартный Note View по Session ID"
    )
    @CommonErrorResponses 
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = NoteResponse.class))
    )
    public NotesResponse getNotes(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id
    ) {
        return noteService.getNotes(id);
    }

    @PostMapping("/{id}/note")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить Note в БД по запросу-созданию и привязать его к сессии"
    )
    @CommonErrorResponses 
    @ApiResponse(responseCode = "409", description = "Сессия в невалидном состоянии",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "201", description = "Note добавлен в БД",
        content = @Content(schema = @Schema(implementation = NoteResponse.class))
    )
    public NoteResponse createNote(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @RequestBody @Valid NoteCreateRequest request
    ) {
        return noteService.createNote(id, request);
    }

    @DeleteMapping ("/{sessionId}/note/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить Note из БД по Note ID и Session ID"
    )
    @CommonErrorResponses 
    @ApiResponse(responseCode = "409", description = "Сессия в невалидном состоянии",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "204", description = "Note удалена из БД")
    public void deleteNote(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID sessionId,
        @Parameter(
            description = "Note ID"
        )
        @PathVariable UUID noteId
    ) {
        noteService.deleteNote(sessionId, noteId);
    }
}
