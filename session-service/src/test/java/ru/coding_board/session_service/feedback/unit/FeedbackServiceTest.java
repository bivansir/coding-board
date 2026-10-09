package ru.coding_board.session_service.feedback.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.coding_board.session_service.common.enums.Decision;
import ru.coding_board.session_service.common.enums.Score;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.feedback.Feedback;
import ru.coding_board.session_service.feedback.FeedbackMapper;
import ru.coding_board.session_service.feedback.FeedbackRepository;
import ru.coding_board.session_service.feedback.FeedbackService;
import ru.coding_board.session_service.feedback.FeedbackTestDataBuilder;
import ru.coding_board.session_service.feedback.dto.FeedbackCreateRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackPatchRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackResponse;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScore;
import ru.coding_board.session_service.feedback.feedback_score.FeedbackScoreBuilder;
import ru.coding_board.session_service.feedback.feedback_score.dto.FeedbackScoreDTO;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;
import ru.coding_board.session_service.session.SessionTestDataBuilder;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {
    @InjectMocks 
    FeedbackService feedbackService;

    @Mock 
    FeedbackRepository feedbackRepository;

    @Mock 
    SessionRepository sessionRepository;

    @Test 
    void shouldGetFeedbackBySessionId() {
        // given
        Feedback feedback = FeedbackTestDataBuilder.aFeedback();
        UUID sessionId = feedback.getSession().getSessionId();
        when(feedbackRepository.findBySessionSessionId(sessionId))
            .thenReturn(Optional.of(feedback));

        // when
        FeedbackResponse result = feedbackService.getFeedbackBySessionId(sessionId);

        // then
        assertEquals(
            FeedbackMapper.buildResponse(feedback), result);
    }

    @Test
    void shouldThrowWhenFeedbackNotFound() {
        // given
        UUID sessionId = UUID.randomUUID();
        when(feedbackRepository.findBySessionSessionId(sessionId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> feedbackService.getFeedbackBySessionId(sessionId));
    }

    @Test
    void shouldCreateFeedbackWithProvidedScoresAndDefaultsForRest() {
        // given
        Session existing = SessionTestDataBuilder.aSessionCompletedWithId().build();
        List<FeedbackScoreDTO> scores = List.of(
            FeedbackScoreBuilder.buildDTO(Score.CODE_QUALITY, 5)
        );
        FeedbackCreateRequest request = new FeedbackCreateRequest(
            scores, Decision.QUESTIONABLE, "Test comment");

        when(feedbackRepository.save(any(Feedback.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when
        feedbackService.createFeedback(existing.getSessionId(), request);

        // then
        ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).save(captor.capture());
        
        Map<Score, FeedbackScore> savedScores = captor.getValue().getScores();
        assertEquals(Score.values().length, savedScores.size());
        assertEquals(5, savedScores.get(Score.CODE_QUALITY).getRank());
        savedScores.entrySet().stream()
            .filter(e -> e.getKey() != Score.CODE_QUALITY)
            .forEach(e -> assertNull(e.getValue().getRank()));
    }

    @Test
    void shouldCreateFullScoreSetWithNullRanksWhenScoresNotProvidedAtAll() {
        // given
        Session existing = SessionTestDataBuilder.aSessionCompletedWithId().build();
        FeedbackCreateRequest request = new FeedbackCreateRequest(
            null, Decision.QUESTIONABLE, null);

        when(feedbackRepository.save(any(Feedback.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when
        feedbackService.createFeedback(existing.getSessionId(), request);

        // then
        ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).save(captor.capture());

        Map<Score, FeedbackScore> savedScores = captor.getValue().getScores();
        assertEquals(Score.values().length, savedScores.size());
        savedScores.entrySet().stream()
            .forEach(e -> assertNull(e.getValue().getRank()));
}

    @Test
    void shouldSaveFeedbackWithOtherFieldsFromRequest() {
        // given
        Session existing = SessionTestDataBuilder.aSessionCompletedWithId().build();
        FeedbackCreateRequest request = new FeedbackCreateRequest(
            null, Decision.QUESTIONABLE, "Test comment");

        when(feedbackRepository.save(any(Feedback.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when
        feedbackService.createFeedback(existing.getSessionId(), request);

        // then
        ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).save(captor.capture());
        Feedback saved = captor.getValue();

        assertEquals(request.decision(), saved.getDecision());
        assertEquals(request.comment(), saved.getComment());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        // given
        FeedbackCreateRequest request = new FeedbackCreateRequest(
            null, Decision.QUESTIONABLE, "Test comment");

        when(sessionRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> feedbackService.createFeedback(UUID.randomUUID(), request));
    }

    @Test
    void shouldReturnResponseBuiltFromSavedFeedback() {
        // given
        Session existing = SessionTestDataBuilder.aSessionCompletedWithId().build();
        FeedbackCreateRequest request = new FeedbackCreateRequest(
            null, Decision.QUESTIONABLE, "Set in Request");
            
        Feedback saved = FeedbackTestDataBuilder.aFeedback();
        saved.setComment("Set in DB Layer");

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(saved);
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        
        // when
        FeedbackResponse response = feedbackService.createFeedback(
            existing.getSessionId(), request);

        // then
        assertEquals(saved.getComment(), response.comment());
        assertNotEquals(request.comment(), response.comment());
        assertEquals(saved.getFeedbackId(), response.feedbackId());
    }

    @Test 
    void shouldUpdateOnlyProvidedFields() {
        // given
        Feedback existing = FeedbackTestDataBuilder.aFeedback();
        FeedbackPatchRequest request = new FeedbackPatchRequest(
            null, Decision.FAILED, null);

        when(feedbackRepository.findBySessionSessionId(
            existing.getSession().getSessionId())).thenReturn(Optional.of(existing));
        when(feedbackRepository.save(any(Feedback.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        feedbackService.patchFeedback(existing.getSession().getSessionId(), request);

        // then
        assertEquals(request.decision(), existing.getDecision());
        assertEquals("Test comment", existing.getComment());
    }

    @Test 
    void shouldUpdateOnlyProvidedFeedbackScoreFields() {
        // given
        Feedback existing = FeedbackTestDataBuilder.aFeedback();
        FeedbackPatchRequest request = new FeedbackPatchRequest(List.of(
            FeedbackScoreBuilder.buildDTO(Score.CODE_QUALITY, 5)),
            null, null);

        when(feedbackRepository.findBySessionSessionId(existing.getSession().getSessionId()))
            .thenReturn(Optional.of(existing));
        when(feedbackRepository.save(any(Feedback.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        feedbackService.patchFeedback(existing.getSession().getSessionId(), request);

        // then
        assertEquals(5, existing.getScores().get(Score.CODE_QUALITY).getRank());
        existing.getScores().entrySet().stream()
            .filter(e -> e.getKey() != Score.CODE_QUALITY)
            .forEach(e -> assertNull(e.getValue().getRank()));
    }

    @Test 
    void shouldThrowWhenFeedbackScoreNotFound() {
        // given
        Feedback existing = FeedbackTestDataBuilder.aFeedback();
        FeedbackPatchRequest request = new FeedbackPatchRequest(List.of(
            FeedbackScoreBuilder.buildDTO(Score.CODE_QUALITY, 5)),
            null, null);

        existing.getScores().remove(Score.CODE_QUALITY);

        when(feedbackRepository.findBySessionSessionId(existing.getSession().getSessionId()))
            .thenReturn(Optional.of(existing));

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> feedbackService.patchFeedback(existing.getSession().getSessionId(), request));
    }
}
