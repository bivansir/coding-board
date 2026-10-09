package ru.coding_board.session_service.feedback;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import ru.coding_board.session_service.common.enums.Decision;
import ru.coding_board.session_service.common.enums.Score;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScore;
import ru.coding_board.session_service.session.Session;

public class FeedbackTestDataBuilder {
    private FeedbackTestDataBuilder() {}

    public static Feedback aFeedback() {
        Feedback feedback = Feedback.builder()
            .feedbackId(UUID.randomUUID())
            .session(minimalSession())
            .decision(Decision.QUESTIONABLE)
            .comment("Test comment")
            .scores(new EnumMap<>(Score.class))
            .build();

        Map<Score, FeedbackScore> scores = Arrays.stream(Score.values())
            .collect(Collectors.<Score, Score, FeedbackScore, EnumMap<Score, FeedbackScore>>toMap(
                score -> score,
                score -> aFeedbackScore().feedback(feedback).score(score).rank(null).build(),
                (a, b) -> a,
                () -> new EnumMap<>(Score.class)
            ));

        feedback.setScores(scores);
        
        return feedback;
    }

    public static FeedbackScore.FeedbackScoreBuilder aFeedbackScore() {
        return FeedbackScore.builder()
            .feedbackScoreId(UUID.randomUUID())
            .feedback(minimalFeedback())
            .score(Score.CODE_QUALITY)
            .rank(null);
    }

    private static Session minimalSession() {
    return Session.builder()
        .sessionId(UUID.randomUUID())
        .build();
    }

    private static Feedback minimalFeedback() {
    return Feedback.builder()
        .feedbackId(UUID.randomUUID())
        .build();
    }
}
