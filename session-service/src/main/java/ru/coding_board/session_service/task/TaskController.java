package ru.coding_board.session_service.task;

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
import ru.coding_board.session_service.session.dto.SessionResponse;
import ru.coding_board.session_service.task.dto.TaskCreateRequest;
import ru.coding_board.session_service.task.dto.TaskFilterRequest;
import ru.coding_board.session_service.task.dto.TaskListItemResponse;
import ru.coding_board.session_service.task.dto.TaskPatchRequest;
import ru.coding_board.session_service.task.dto.TaskResponse;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task API", description = "API для работы с сущностью Task")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить стандартный Task View по Task ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = TaskResponse.class))
    )
    public TaskResponse getSessionTasksBySessionId(
        @Parameter(
            description = "Task ID"
        )
        @PathVariable UUID id
    ) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    @Operation(
            summary = "Получить Task View's списка по запросу-фильтру"
    )
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Невалидный запрос",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(
            implementation = Page.class,
            subTypes  = TaskListItemResponse.class))
    )
    public Page<TaskListItemResponse> filterTasks(
        @Valid TaskFilterRequest request,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return taskService.filterTasks(request, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить Task в БД по запросу-созданию"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "409", description = "Нарушена уникальность title",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(responseCode = "201", description = "Task добавлена в БД",
        content = @Content(schema = @Schema(implementation = TaskResponse.class))
    )
    public TaskResponse createTask(
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @Valid @RequestBody TaskCreateRequest request
    ) {
        return taskService.createTask(request);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновить поля Task в БД по патч-запросу"
    )
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = SessionResponse.class))
    )
    public TaskResponse patchTask(
        @Parameter(
            description = "Task ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Патч-запрос DTO"
        )
        @Valid @RequestBody TaskPatchRequest request
    ) {
        return taskService.patchTask(request, id);
    }
}
