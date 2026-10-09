package ru.coding_board.session_service.session.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateTestDataBuilder;
import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionMapper;
import ru.coding_board.session_service.position.PositionTestDataBuilder;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionBuilder;
import ru.coding_board.session_service.session.SessionCancellation;
import ru.coding_board.session_service.session.SessionTestDataBuilder;
import ru.coding_board.session_service.session.dto.SessionCancellationCreateRequest;
import ru.coding_board.session_service.session.dto.SessionCreateRequest;
import ru.coding_board.session_service.session.dto.SessionResponse;

public class SessionBuilderTest {
    @Test
    void shouldBuildSession() {
        // given
        Position position = PositionTestDataBuilder.aPoisitionWithId().build();
        Candidate candidate = CandidateTestDataBuilder.aCandidateWithId().build();
        
        SessionCreateRequest request = new SessionCreateRequest(
            position.getPositionId(), candidate.getCandidateId(),
            UUID.randomUUID(), LocalDateTime.now());

        // when
        Session session = SessionBuilder.buildSession(request, position, candidate);

        // then
        assertEquals(candidate, session.getCandidate());
        assertEquals(position, session.getPosition());
        assertEquals(request.interviewerId(), session.getInterviewerId());
        assertEquals(request.scheduledAt(), session.getScheduledAt());

        assertNull(session.getInviteToken());
        assertNull(session.getStartedAt());
        assertNull(session.getCompletedAt());
    }

    @Test
    void shouldBuildSessionCancellation() {
        // given
        Session session = SessionTestDataBuilder.aSessionScheduledWithId().build();
        SessionCancellationCreateRequest request = new SessionCancellationCreateRequest(
            "Test", UUID.randomUUID(), LocalDateTime.now());

        // when
        SessionCancellation sessionCancellation = SessionBuilder.buildSessionCancellation(
            session, request);

        // then
        assertEquals(session, sessionCancellation.getSession());
        assertEquals(request.cancellationReason(), sessionCancellation.getCancellationReason());
        assertEquals(request.hrId(), sessionCancellation.getHrId());
        assertEquals(request.cancellationTime(), sessionCancellation.getCancellationTime());
    }

    @Test
    void shouldBuildSessionResponse() {
        // given
        Session session = SessionTestDataBuilder.aSessionScheduledWithId().build();
        Position position = session.getPosition();

        // when
        SessionResponse sessionResponse = SessionBuilder.buildResponse(session, position);

        // then
        assertEquals(session.getSessionId(), sessionResponse.sessionId());
        assertEquals(session.getSessionStatus(), sessionResponse.status());
        assertEquals(PositionMapper.buildResponse(session.getPosition()),
            sessionResponse.positionResponse());
        assertEquals(session.getInterviewerId(), sessionResponse.interviewerId());
        assertEquals(session.getCandidate().getCandidateId(), sessionResponse.candidateId());
        assertEquals(session.getInviteToken(), sessionResponse.inviteToken());
        assertEquals(session.getScheduledAt(), sessionResponse.scheduledAt());
        assertEquals(session.getCompletedAt(), sessionResponse.completedAt());
        assertEquals(session.getStartedAt(), sessionResponse.startedAt());
        assertNull(sessionResponse.sessionCancellation());
    }

    @Test
    void shouldBuildSessionResponseWithSessionCancellation() {
        // given
        Session session = SessionTestDataBuilder.aSessionScheduledWithId().build();
        Position position = session.getPosition();
        SessionCancellation sessionCancellation = SessionCancellation.builder()
            .session(session)
            .cancellationReason("TEST")
            .hrId(UUID.randomUUID())
            .cancellationTime(LocalDateTime.now())
            .build();

        // when
        SessionResponse sessionResponse = SessionBuilder.buildResponse(
            session, sessionCancellation, position);

        // then
        assertEquals(sessionCancellation.getCancellationReason(),
            sessionResponse.sessionCancellation().cancellationReason());
        assertEquals(sessionCancellation.getHrId(),
            sessionResponse.sessionCancellation().hrId());
        assertEquals(sessionCancellation.getCancellationTime(),
            sessionResponse.sessionCancellation().cancellationTime());
    }
}
