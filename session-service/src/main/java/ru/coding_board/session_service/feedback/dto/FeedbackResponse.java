package ru.coding_board.session_service.feedback.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.coding_board.session_service.common.enums.Decision;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;

@Builder
public record FeedbackResponse(

    @Schema(description = "ID фидбека", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID feedbackId,

    @Schema(description = "ID сессии", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID sessionId,

    @Schema(description = "Решение по итогам интервью", example = "QUESTIONABLE")
    Decision decision,

    @Schema(description = "Комментарий интервьюера к фидбеку", 
        example = "Продемонстрировал углубленное понимание Java Core для своего грейда")
    String comment,

    @Schema(description = "Список оценок кандидата по критериям")
    List<FeedbackScoreDTO> scores
) {}
