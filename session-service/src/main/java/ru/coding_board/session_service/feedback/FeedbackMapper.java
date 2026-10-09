package ru.coding_board.session_service.feedback;

import ru.coding_board.session_service.feedback.dto.FeedbackCreateRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackResponse;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScoreBuilder;
import ru.coding_board.session_service.session.Session;

public final class FeedbackMapper {
    private FeedbackMapper() {}

    // scores присваивается в feedbackService
    public static Feedback buildFeedback(FeedbackCreateRequest request, Session session) {
        return Feedback.builder()
            .session(session)
            .decision(request.decision())
            .comment(request.comment())
            .build();
    }

    public static FeedbackResponse buildResponse(Feedback feedback) {
        return FeedbackResponse.builder()
            .feedbackId(feedback.getFeedbackId())
            .decision(feedback.getDecision())
            .comment(feedback.getComment())
            .scores(feedback.getScores().values().stream()
                .map(s -> FeedbackScoreBuilder.buildDTO(s.getScore(), s.getRank()))
                .toList()
            )
            .build();
    }
}
