package ru.coding_board.session_service.feedback.feedback_score;

import ru.coding_board.session_service.common.enums.Score;
import ru.coding_board.session_service.feedback.Feedback;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;

public final class FeedbackScoreBuilder {
    private FeedbackScoreBuilder() {}

    public static FeedbackScoreDTO buildDTO(Score score, Integer rank) {
        return FeedbackScoreDTO.builder()
            .score(score)
            .rank(rank)
            .build();
    }

    public static FeedbackScore buildFeedbackScore(Feedback feedback, Score score, Integer rank) {
        return FeedbackScore.builder()
            .feedback(feedback)
            .score(score)
            .rank(rank)
            .build();
    }
}
