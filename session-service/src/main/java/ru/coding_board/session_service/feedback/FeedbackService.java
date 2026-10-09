package ru.coding_board.session_service.feedback;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.common.enums.Score;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.common.util.RepositoryUtils;
import ru.coding_board.session_service.feedback.dto.FeedbackCreateRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackPatchRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackResponse;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScore;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScoreBuilder;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;

@RequiredArgsConstructor
@Service
public class FeedbackService {
    private FeedbackRepository feedbackRepository;
    private SessionRepository sessionRepository;

    public FeedbackResponse getFeedbackBySessionId(UUID sessionId) { 
        return FeedbackMapper.buildResponse(getBySessionId(sessionId));
    }

    @Transactional
    public FeedbackResponse createFeedback(UUID sessionId, FeedbackCreateRequest request) {
        Session session = getSessionById(sessionId);
        Feedback feedback = FeedbackMapper.buildFeedback(request, session);

        Map<Score, FeedbackScore> scores = new EnumMap<>(Score.class);
        for (Score score : Score.values()) {
            scores.put(score, FeedbackScoreBuilder.buildFeedbackScore(feedback, score, null));
        }

        if (request.scores() != null) {
            for (FeedbackScoreDTO feedbackScoreDTO : request.scores()) {
                scores.get(feedbackScoreDTO.score()).setRank(feedbackScoreDTO.rank());
            }
        }
        feedback.setScores(scores);

        return FeedbackMapper.buildResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public FeedbackResponse patchFeedback(UUID sessionId, FeedbackPatchRequest request) {
        Feedback feedback = getBySessionId(sessionId);

        if (request.scores() != null) {
            for (FeedbackScoreDTO scoreDto : request.scores()) {
                FeedbackScore feedbackScore = feedback.getScores().get(scoreDto.score());
                if (feedbackScore == null) {
                    throw new ServiceEntityNotFoundException(FeedbackScore.class,
                        Map.of("feedbackId", feedback.getFeedbackId(),
                         "score", scoreDto.score()));
                }
                feedbackScore.setRank(scoreDto.rank());
            }
        }

        if (request.decision() != null) feedback.setDecision(request.decision());
        if (request.comment() != null) feedback.setComment(request.comment());

        Feedback saved = feedbackRepository.save(feedback);
        return FeedbackMapper.buildResponse(saved);
    }

    private Feedback getBySessionId(UUID sessionId) {
        Feedback feedback = feedbackRepository.findBySessionSessionId(sessionId)
            .orElseThrow(() -> new ServiceEntityNotFoundException(
                Feedback.class, Map.of("sessionId", sessionId)));

        return feedback;
    }

    private Session getSessionById(UUID id) {
        Session session = RepositoryUtils.findOrThrow(
            sessionRepository, id, Session.class);
        return session;
    }
}
