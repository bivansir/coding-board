package ru.coding_board.session_service.feedback.feedback_score.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import ru.coding_board.session_service.common.enums.Score;

@Builder
public record FeedbackScoreDTO(

    @Schema(description = "Тип оценки", example = "CODE_QUALITY")
    Score score,

    @Schema(description = "Оценка", example = "5")
    @Min(0)
    @Max(5)
    Integer rank
) {
    
}
