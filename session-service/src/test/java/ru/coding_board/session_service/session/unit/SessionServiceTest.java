package ru.coding_board.session_service.session.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateRepository;
import ru.coding_board.session_service.candidate.CandidateTestDataBuilder;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.common.exception.InvalidSessionStatusException;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;
import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionRepository;
import ru.coding_board.session_service.position.PositionTestDataBuilder;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionBuilder;
import ru.coding_board.session_service.session.SessionCancellation;
import ru.coding_board.session_service.session.SessionCancellationRepository;
import ru.coding_board.session_service.session.SessionInviteTokenGenerator;
import ru.coding_board.session_service.session.SessionRepository;
import ru.coding_board.session_service.session.SessionService;
import ru.coding_board.session_service.session.SessionTestDataBuilder;
import ru.coding_board.session_service.session.dto.SessionCancellationCreateRequest;
import ru.coding_board.session_service.session.dto.SessionCreateRequest;
import ru.coding_board.session_service.session.dto.SessionFilterRequest;
import ru.coding_board.session_service.session.dto.SessionResponse;
import ru.coding_board.session_service.session.dto.SessionScheduledPatchRequest;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {
    @InjectMocks 
    SessionService sessionService;

    @Mock
    SessionRepository sessionRepository;

    @Mock
    SessionCancellationRepository sessionCancellationRepository;

    @Mock
    PositionRepository positionRepository;

    @Mock
    CandidateRepository candidateRepository;

    @Mock
    SessionInviteTokenGenerator tokenGenerator;

    @Test 
    void shouldReturnSessionById() {
        // given
        Session session = SessionTestDataBuilder.aSessionScheduledWithId().build();
        UUID sessionId = session.getSessionId();
        when(sessionRepository.findById(sessionId))
            .thenReturn(Optional.of(session));

        // when
        SessionResponse result = sessionService.getSessionById(sessionId);

        // then
        assertEquals(SessionBuilder.buildResponse(session, 
            session.getPosition()), result);
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        // given
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findById(sessionId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionService.getSessionById(sessionId));
    }

    @Test
    void shouldFilterSessionsAndReturnItsResult() {
        // given
        Session existing = SessionTestDataBuilder.aSessionCompletedWithId().build();
        SessionFilterRequest request = new SessionFilterRequest(
            null, null, null,
            SessionStatus.COMPLETED, null, null
        );
        PageRequest pageable = PageRequest.of(1, 5);
        Page<SessionResponse> exeptedPage = new PageImpl<>(List.of(
            SessionBuilder.buildResponse(existing, existing.getPosition())));

        when(sessionRepository.findAll(ArgumentMatchers.<Specification<Session>>any(), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(
                existing)));

        // when
        Page<SessionResponse> result = sessionService.filterSessions(request, pageable);

        // then
        assertEquals(exeptedPage, result);
    }

    @Test
    void shouldSaveSessionWithFieldsFromRequest() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();
        Candidate candidate = CandidateTestDataBuilder.aCandidate().build();

        SessionCreateRequest request = new SessionCreateRequest(
            position.getPositionId(), candidate.getCandidateId(),
            UUID.randomUUID(), LocalDateTime.now());
        
        when(positionRepository.findById(position.getPositionId()))
            .thenReturn(Optional.of(position));
        when(candidateRepository.findById(candidate.getCandidateId()))
            .thenReturn(Optional.of(candidate));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(tokenGenerator.generate()).thenReturn("SecretToken");

        // when
        sessionService.createSession(request);

        // then
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        Session saved = captor.getValue();

        assertEquals(position, saved.getPosition());
        assertEquals(candidate, saved.getCandidate());
        assertEquals(request.interviewerId(), saved.getInterviewerId());
        assertEquals(request.scheduledAt(), saved.getScheduledAt());

        assertNull(saved.getCompletedAt());
        assertNull(saved.getStartedAt());
    }

    @Test
    void shouldAlwaysSetNewSessionRandomInviteToken() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();
        Candidate candidate = CandidateTestDataBuilder.aCandidateWithId().build();

        SessionCreateRequest request = new SessionCreateRequest(
            position.getPositionId(), candidate.getCandidateId(),
            UUID.randomUUID(), LocalDateTime.now());
        
        when(positionRepository.findById(position.getPositionId()))
            .thenReturn(Optional.of(position));
        when(candidateRepository.findById(candidate.getCandidateId()))
            .thenReturn(Optional.of(candidate));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(tokenGenerator.generate()).thenReturn("SecretToken");

        // when
        sessionService.createSession(request);

        // then
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        Session saved = captor.getValue();

        assertNotNull(saved.getInviteToken());
        verify(tokenGenerator).generate();
    }

    @Test
    void shouldAlwaysSetNewSessionScheduledStatus() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();
        Candidate candidate = CandidateTestDataBuilder.aCandidateWithId().build();

        SessionCreateRequest request = new SessionCreateRequest(
            position.getPositionId(), candidate.getCandidateId(),
            UUID.randomUUID(), LocalDateTime.now());
        
        when(positionRepository.findById(position.getPositionId()))
            .thenReturn(Optional.of(position));
        when(candidateRepository.findById(candidate.getCandidateId()))
            .thenReturn(Optional.of(candidate));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(tokenGenerator.generate()).thenReturn("SecretToken");

        // when
        sessionService.createSession(request);

        // then
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        Session saved = captor.getValue();

        assertEquals(SessionStatus.SCHEDULED, saved.getSessionStatus());
    }

    @Test
    void shouldReturnResponseBuiltFromSavedSession() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();

        Candidate candidateRequest = CandidateTestDataBuilder.aCandidateWithId().build();
        Candidate candidateDb = CandidateTestDataBuilder.aCandidateWithId().build();

        SessionCreateRequest request = new SessionCreateRequest(
            position.getPositionId(), candidateRequest.getCandidateId(),
            UUID.randomUUID(), LocalDateTime.now());
        Session saved =  SessionTestDataBuilder.aSessionScheduledWithId().candidate(candidateDb).build();

        when(positionRepository.findById(position.getPositionId()))
            .thenReturn(Optional.of(position));
        when(candidateRepository.findById(candidateRequest.getCandidateId()))
            .thenReturn(Optional.of(candidateRequest));
        when(sessionRepository.save(any(Session.class))).thenReturn(saved);

        // when
        SessionResponse response = sessionService.createSession(request);

        // then
        assertEquals(saved.getCandidate().getCandidateId(), response.candidateId());
        assertNotEquals(request.candidateId(), response.candidateId());
        assertEquals(saved.getCandidate().getCandidateId(), response.candidateId());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        // given
        Session existing =  SessionTestDataBuilder.aSessionScheduledWithId().build();
        Position position = existing.getPosition();
        Candidate candidate = existing.getCandidate();
        UUID interviewerId = existing.getInterviewerId();
        String inviteToken = existing.getInviteToken();

        SessionScheduledPatchRequest request = new SessionScheduledPatchRequest(
            null, null,
            null, LocalDateTime.now());

        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when
        sessionService.patchScheduledSession(existing.getSessionId(), request);

        // then
        assertEquals(request.scheduledAt(), existing.getScheduledAt());
        assertEquals(position, existing.getPosition());
        assertEquals(candidate, existing.getCandidate());
        assertEquals(interviewerId, existing.getInterviewerId());
        assertEquals(inviteToken, existing.getInviteToken());

        assertNull(existing.getCompletedAt());
        assertNull(existing.getStartedAt());
    }

    @Test
    void shouldThrowWhenPositionNotFound() {
        // given
        Session existing =  SessionTestDataBuilder.aSessionScheduledWithId().build();
        UUID positionId = UUID.randomUUID();
        SessionScheduledPatchRequest request = new SessionScheduledPatchRequest(
            positionId, null, null, null);

        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(positionRepository.findById(positionId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionService.patchScheduledSession(existing.getSessionId(), request));
    }

    @Test
    void shouldThrowWhenCandidateNotFound() {
        // given
        Session existing =  SessionTestDataBuilder.aSessionScheduledWithId().build();
        UUID candidateId = UUID.randomUUID();
        SessionScheduledPatchRequest request = new SessionScheduledPatchRequest(
            null, candidateId, null, null);

        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(candidateRepository.findById(candidateId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> sessionService.patchScheduledSession(existing.getSessionId(), request));
    }

    @Test
    void shouldThrowWhenTryingToPatchAndSessionStatusNotScheduled() {
        // given
        Session existing =  SessionTestDataBuilder.aSessionCompletedWithId().build();
        UUID candidateId = UUID.randomUUID();
        SessionScheduledPatchRequest request = new SessionScheduledPatchRequest(
            null, candidateId, null, null);

        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when then
        assertThrows(InvalidSessionStatusException.class,
            () -> sessionService.patchScheduledSession(existing.getSessionId(), request));
    }

    @Test
    void shouldGenInviteTokenAndAssignItToSession() {
        // given
        Session existing =  SessionTestDataBuilder.aSessionScheduledWithId().build();
        String inviteToken = existing.getInviteToken();

        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(tokenGenerator.generate()).thenReturn("SecretToken");

        // when
        sessionService.genInviteToken(existing.getSessionId());

        // then
        assertNotEquals(inviteToken, existing.getInviteToken());
        verify(tokenGenerator).generate();
    }

    @Test
    void shouldTransitToOngoing() {
        // given
        Session existing =  SessionTestDataBuilder.aSessionScheduledWithId().build();
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        sessionService.startSession(existing.getSessionId());

        // then
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        assertEquals(SessionStatus.ONGOING, captor.getValue().getSessionStatus());
    }

    @Test
    void shouldTransitToCompleted() {
        // given
        Session existing = SessionTestDataBuilder.aSessionScheduledWithId()
            .sessionStatus(SessionStatus.ONGOING).build();
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        sessionService.completeSession(existing.getSessionId(), LocalDateTime.now());

        // then
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        assertEquals(SessionStatus.COMPLETED, captor.getValue().getSessionStatus());
    }

    @Test
    void shouldCompleteSessionWithProvidedTime() {
        // given
        Session existing = SessionTestDataBuilder.aSessionScheduledWithId()
            .sessionStatus(SessionStatus.ONGOING).build();
        LocalDateTime providedTime = LocalDateTime.of(
            2026, 1, 15, 10, 0);
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        sessionService.completeSession(existing.getSessionId(), providedTime);

        // then
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        assertEquals(providedTime, captor.getValue().getCompletedAt());
    }

    @Test
    void shouldCompleteSessionWithNowWhenTimeNotProvided() {
        // given
        Session existing = SessionTestDataBuilder.aSessionScheduledWithId()
            .sessionStatus(SessionStatus.ONGOING).build();
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now();

        // when
        sessionService.completeSession(existing.getSessionId(), null);

        // then
        LocalDateTime after = LocalDateTime.now();
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());

        LocalDateTime completedAt = captor.getValue().getCompletedAt();
        assertNotNull(completedAt);
        assertFalse(completedAt.isBefore(before));
        assertFalse(completedAt.isAfter(after));
    }

    @Test
    void shouldThrowWhenCompletingFromScheduled() {
        // given
        Session existing = SessionTestDataBuilder.aSessionScheduledWithId().build();
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when then
        assertThrows(InvalidSessionStatusException.class,
            () -> sessionService.completeSession(existing.getSessionId(), LocalDateTime.now()));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldTransitToCancelledAndCreateSessionCancellation() {
        // given
        Session existing = SessionTestDataBuilder.aSessionScheduledWithId().build();
        SessionCancellationCreateRequest request = new SessionCancellationCreateRequest(
            "Candidate withdrew", UUID.randomUUID(), LocalDateTime.now());

        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));
        when(sessionCancellationRepository.save(any(SessionCancellation.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.save(any(Session.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        sessionService.cancelSession(existing.getSessionId(), request);

        // then
        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        assertEquals(SessionStatus.CANCELLED, sessionCaptor.getValue().getSessionStatus());

        ArgumentCaptor<SessionCancellation> cancellationCaptor = ArgumentCaptor.forClass(
            SessionCancellation.class);
        verify(sessionCancellationRepository).save(cancellationCaptor.capture());
        assertEquals(existing, cancellationCaptor.getValue().getSession());
    }

    @Test
    void shouldThrowWhenTransitioningFromCompleted() {
        // given
        Session existing = SessionTestDataBuilder.aSessionCompletedWithId().build();
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when then
        assertThrows(InvalidSessionStatusException.class,
            () -> sessionService.startSession(existing.getSessionId()));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTransitioningFromCancelled() {
        // given
        Session existing = SessionTestDataBuilder.aSessionScheduledWithId()
            .sessionStatus(SessionStatus.CANCELLED).build();
        when(sessionRepository.findById(existing.getSessionId()))
            .thenReturn(Optional.of(existing));

        // when then
        assertThrows(InvalidSessionStatusException.class,
            () -> sessionService.startSession(existing.getSessionId()));

        verify(sessionRepository, never()).save(any());
    }
}

