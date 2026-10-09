package ru.coding_board.session_service.session;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import ru.coding_board.session_service.common.exception.ErrorResponse;
import ru.coding_board.session_service.session.dto.SessionCancellationCreateRequest;
import ru.coding_board.session_service.session.dto.SessionCreateRequest;
import ru.coding_board.session_service.session.dto.SessionFilterRequest;
import ru.coding_board.session_service.session.dto.SessionResponse;
import ru.coding_board.session_service.session.dto.SessionScheduledPatchRequest;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Session API", description = "API для работы с сущностью Session")
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить стандартный Session View по Session ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    public SessionResponse getSessionByID(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id
    ) {
        return sessionService.getSessionById(id);
    }

    @GetMapping
    @Operation(
            summary = "Получить Session View's по запросу-фильтру"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(
            implementation = Page.class,
            subTypes  = SessionResponse.class))
    )
    public Page<SessionResponse> filterSessions(
        @Parameter(
            description = "Запрос-фильтр DTO"
        )
        @Valid SessionFilterRequest request,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return sessionService.filterSessions(request, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CommonErrorResponses
    @ApiResponse(responseCode = "201", description = "Сессия добавлена в БД",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    @Operation(
            summary = "Добавить Session в БД по запросу-созданию"
    )
    public SessionResponse createSession(
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @RequestBody @Valid SessionCreateRequest request
    ) {
        return sessionService.createSession(request);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновить поля Session в БД по патч-запросу"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Сессия в невалидном состоянии",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "200", description = "Сессия добавлена в БД",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    public SessionResponse patchSession(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Патч-запрос DTO"
        )
        @RequestBody @Valid SessionScheduledPatchRequest request
    ) {
        return sessionService.patchScheduledSession(id, request);
    }

    @PatchMapping ("/{id}/invite-token")
    @Operation(
            summary = "Создать новый inviteToken для Session"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "204", description = "Новый токен сгенерирован")
    public String genInviteToken(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id
    ) {
        return sessionService.genInviteToken(id);
    }

    @PatchMapping ("/{id}/cancel")
    @Operation(
            summary = "Отменить сессию по Session ID с созданием SessionCancellation"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Сессия в невалидном состоянии",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "200", description = "Сессия добавлена в БД",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    public SessionResponse cancelSession(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @Valid @RequestBody SessionCancellationCreateRequest request
    ) {
        return sessionService.cancelSession(id, request);
    }

    @PatchMapping ("/{id}/start")
    @Operation(
            summary = "Начать сессию по Session ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Сессия в невалидном состоянии",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "200", description = "Сессия начата",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    public SessionResponse startSession(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id
    ) {
        return sessionService.startSession(id);
    }

    @PatchMapping ("/{id}/end")
    @Operation(
            summary = "Закончить сессию по Session ID и времени"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Сессия в невалидном состоянии",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "200", description = "Сессия окончена",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    public SessionResponse endSession(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Время окончания сессии"
        )
        @RequestParam(required = false) LocalDateTime time
    ) {
        return sessionService.completeSession(id, time);
    }
}
