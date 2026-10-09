package ru.coding_board.session_service.feedback.feedback_score.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.common.enums.Score;
import ru.coding_board.session_service.feedback.Feedback;
import ru.coding_board.session_service.feedback.FeedbackTestDataBuilder;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScore;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScoreBuilder;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;

public class FeedbackScoreBuilderTest {
    @Test
    void shouldBuildFeedbackScoreDTO() {
        // given
        Score score = Score.CODE_QUALITY;
        Integer rank = 5;

        // when
        FeedbackScoreDTO builtFeedbackScoreDTO = FeedbackScoreBuilder.buildDTO(score, rank);

        // then
        assertEquals(score, builtFeedbackScoreDTO.score());
        assertEquals(rank, builtFeedbackScoreDTO.rank());
    }

    @Test
    void shouldBuildFeedbackScore() {
        // given
        Feedback feedback = FeedbackTestDataBuilder.aFeedback();
        Score score = Score.CODE_QUALITY;
        Integer rank = 5;

        // when
        FeedbackScore builtFeedbackScore = FeedbackScoreBuilder.buildFeedbackScore(
            feedback, score, rank);

        // then
        assertEquals(feedback, builtFeedbackScore.getFeedback());
        assertEquals(score, builtFeedbackScore.getScore());
        assertEquals(rank, builtFeedbackScore.getRank());
    }
}
