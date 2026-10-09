package ru.coding_board.session_service.feedback.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.coding_board.session_service.common.enums.Decision;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;

public record FeedbackPatchRequest(
    @Schema(description = "Список оценок кандидата по критериям")
    List<FeedbackScoreDTO> scores,

    @Schema(description = "Решение по итогам интервью", example = "QUESTIONABLE")
    Decision decision,

    @Schema(description = "Комментарий интервьюера к фидбеку", 
        example = "Продемонстрировал углубленное понимание Java Core для своего грейда")
    String comment
) {}
