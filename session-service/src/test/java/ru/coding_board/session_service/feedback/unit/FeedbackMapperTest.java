package ru.coding_board.session_service.feedback.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.common.enums.Decision;
import ru.coding_board.session_service.common.enums.Score;
import ru.coding_board.session_service.feedback.Feedback;
import ru.coding_board.session_service.feedback.FeedbackMapper;
import ru.coding_board.session_service.feedback.FeedbackTestDataBuilder;
import ru.coding_board.session_service.feedback.dto.FeedbackCreateRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackResponse;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScoreBuilder;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionTestDataBuilder;

public class FeedbackMapperTest {
    @Test
    void shouldBuildFeedbackFromRequestAndSession() {
        // given
        Session session = SessionTestDataBuilder.aSessionCompletedWithId().build();
        List<FeedbackScoreDTO> scores = List.of(
                FeedbackScoreBuilder.buildDTO(Score.CODE_QUALITY, 5),
                FeedbackScoreBuilder.buildDTO(Score.COMMUNICATION, 3));

        FeedbackCreateRequest request = new FeedbackCreateRequest(
            scores,
            Decision.QUESTIONABLE,
            "Test comment"
        );

        // when
        Feedback feedback = FeedbackMapper.buildFeedback(request, session);

        // then
        assertEquals(Map.of(), feedback.getScores());
        assertEquals(request.decision(), feedback.getDecision());
        assertEquals(request.comment(), feedback.getComment());

        assertEquals(session, feedback.getSession());
        
    }

    @Test
    void shouldMapFeedbackToResponse() {
        // given
        Feedback feedback = FeedbackTestDataBuilder.aFeedback();

        // when
        FeedbackResponse response = FeedbackMapper.buildResponse(feedback);

        // then
        assertEquals(feedback.getFeedbackId(), response.feedbackId());
        assertEquals(feedback.getDecision(),  response.decision());
        assertEquals(feedback.getComment(), response.comment());
        
        assertEquals(Score.values().length, response.scores().size());
        FeedbackScoreDTO codeQuality = response.scores().stream()
            .filter(dto -> dto.score() == Score.CODE_QUALITY)
            .findFirst().orElseThrow();
        assertNull(codeQuality.rank());
    }
}
