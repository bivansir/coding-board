package ru.coding_board.session_service.session.session_task;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import ru.coding_board.session_service.session.session_task.dto.SessionTasksCreateRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksPatchRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksResponse;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "SessionTask API", description = "API для работы с сущностью SessionTask")
public class SessionTaskController {
    private final SessionTaskService sessionTaskService;

    @GetMapping("/{id}/task")
    @Operation(
            summary = "Получить список SessionTask View's по Session ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = SessionTasksResponse.class))
    )
    public SessionTasksResponse getSessionTasksBySessionId(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id
    ) {
        return sessionTaskService.getSessionTasksBySessionId(id);
    }

    @PostMapping("/{id}/task")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить батч SessionTask в БД по запросу-созданию"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "201", description = "Батч добавлен",
        content = @Content(schema = @Schema(implementation = SessionTasksResponse.class))
    )
    public SessionTasksResponse createSessionTasks(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @Valid @RequestBody SessionTasksCreateRequest request
    ) {
        return sessionTaskService.createSessionTasks(id, request);
    }

    @DeleteMapping ("/{sessionId}/task")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить батч SessionTask из БД по Session ID и SessionTask ID's"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "204", description = "Батч удален")
    public void deleteSessionTasks(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID sessionId,
        @Parameter(
            description = "Список SessionTask ID"
        )
        @RequestParam List<UUID> sessionTaskIds
    ) {
        sessionTaskService.deleteSessionTasks(sessionId, sessionTaskIds);
    }

    @PatchMapping("/{id}/task")
    @Operation(
            summary = "Обновить батч SessionTask в БД по списку патч-запросов и Session ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = SessionTasksResponse.class))
    )
    public SessionTasksResponse patchSessionTasks(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Батч патч-запросов"
        )
        @Valid @RequestBody List<SessionTasksPatchRequest> request
    ) {
        return sessionTaskService.patchSessionTasks(id, request);
    }
}
