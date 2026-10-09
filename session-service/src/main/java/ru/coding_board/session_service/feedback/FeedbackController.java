package ru.coding_board.session_service.feedback;

import java.util.UUID;

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
import ru.coding_board.session_service.feedback.dto.FeedbackCreateRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackPatchRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackResponse;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Feedback API", description = "API для работы с сущностью Feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @GetMapping("/{id}/feedback")
    @Operation(
            summary = "Получить стандартный Feedback View по Session ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))
    )
    public FeedbackResponse getFeedbackById(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id
    ) {
        return feedbackService.getFeedbackBySessionId(id);
    }

    @CommonErrorResponses
    @ApiResponse(responseCode = "201", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))
    )
    @PostMapping("/{id}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить Feedback в БД по запросу-созданию и привязать его к сессии"
    )
    public FeedbackResponse createFeedback(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Запрос-создание DTO"
        )
        @RequestBody @Valid FeedbackCreateRequest request
    ) {
        return feedbackService.createFeedback(id, request);
    }

    @PatchMapping("/{id}/feedback")
    @Operation(
            summary = "Обновить поля Feedback в БД по патч-запросу и Feedback ID"
    )
    @CommonErrorResponses
    @ApiResponse(responseCode = "200", description = "Запрос выполнен",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))
    )
    public FeedbackResponse patchFeedback(
        @Parameter(
            description = "Session ID"
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Патч-запрос DTO"
        )
        @RequestBody @Valid FeedbackPatchRequest request
    ) {
        return feedbackService.patchFeedback(id, request);
    }
}
