package ru.coding_board.session_service.session.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityManager;
import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateTestDataBuilder;
import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionTestDataBuilder;
import ru.coding_board.session_service.session.Session;
import ru.coding_board.session_service.session.SessionRepository;
import ru.coding_board.session_service.session.SessionSpecifications;
import ru.coding_board.session_service.session.SessionTestDataBuilder;
import ru.coding_board.session_service.util.AbstractIntegrationTest;

public class SessionSpecificationsIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    private EntityManager entityManager;

    private Position samplePosition;
    private Candidate sampleCandidate;

    @BeforeEach 
    void setUp() {
        samplePosition = PositionTestDataBuilder.aPosition().build();
        sampleCandidate = CandidateTestDataBuilder.aCandidate().build();

        entityManager.persist(samplePosition);
        entityManager.persist(sampleCandidate);
        entityManager.flush();
    }

    @Test 
    void scheduledAfterShouldIncludeSessionsOnExactBoundaryDate() {
        // given
        Session sessionNotFound = SessionTestDataBuilder.aSessionScheduled()
            .position(samplePosition).candidate(sampleCandidate).scheduledAt(LocalDateTime.of(
                LocalDate.of(2026, 1, 1), LocalTime.MIN)).build();
        Session sessionFound = SessionTestDataBuilder.aSessionScheduled()
            .position(samplePosition).candidate(sampleCandidate).scheduledAt(LocalDateTime.of(
                LocalDate.of(2026, 1, 2), LocalTime.MIN)).build();

        entityManager.persist(sessionFound);
        entityManager.persist(sessionNotFound);
        entityManager.flush();

        // when
        List<Session> result = sessionRepository.findAll(
            SessionSpecifications.scheduledAfter(LocalDate.of(2026, 1, 2)));

        // then
        assertEquals(1, result.size());
        assertEquals(sessionFound.getSessionId(), result.get(0).getSessionId());
    }

    @Test 
    void scheduledBeforeShouldIncludeSessionsOnExactBoundaryDate() {
        // given
        Session sessionFound = SessionTestDataBuilder.aSessionScheduled()
            .position(samplePosition).candidate(sampleCandidate).scheduledAt(LocalDateTime.of(
                LocalDate.of(2026, 1, 1), LocalTime.MIN)).build();
        Session sessionNotFound = SessionTestDataBuilder.aSessionScheduled()
            .position(samplePosition).candidate(sampleCandidate).scheduledAt(LocalDateTime.of(
                LocalDate.of(2026, 1, 2), LocalTime.MIN)).build();

        entityManager.persist(sessionFound);
        entityManager.persist(sessionNotFound);
        entityManager.flush();

        // when
        List<Session> result = sessionRepository.findAll(
            SessionSpecifications.scheduledBefore(LocalDate.of(2026, 1, 1)));

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(sessionFound));
    }

    @Test
    void hasPositionInShouldFilterByGivenIds() {
        // given
        Position position1 = PositionTestDataBuilder.aPosition().build();
        Position position2 = PositionTestDataBuilder.aPosition().build();

        Session session1 = SessionTestDataBuilder.aSessionCompleted()
            .position(position1).candidate(sampleCandidate).build();
        Session session2 = SessionTestDataBuilder.aSessionCompleted()
            .position(position2).candidate(sampleCandidate).build();
        Session sessionNotFound = SessionTestDataBuilder.aSessionCompleted()
            .position(samplePosition).candidate(sampleCandidate).build();

        entityManager.persist(position1);
        entityManager.persist(position2);
        entityManager.persist(session1);
        entityManager.persist(session2);
        entityManager.persist(sessionNotFound);
        entityManager.flush();

        // when
        List<Session> result = sessionRepository.findAll(
            SessionSpecifications.hasPositionIn(
                List.of(position1.getPositionId(), position2.getPositionId())));

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(session1));
        assertTrue(result.contains(session2));
    }

    @Test
    void interviewerIdEqualsShouldFilterByGivenInterviewerId() {
        // given
        Session sessionFound = SessionTestDataBuilder.aSessionCompleted()
            .position(samplePosition).candidate(sampleCandidate).build();
        Session sessionNotFound = SessionTestDataBuilder.aSessionCompleted()
            .position(samplePosition).candidate(sampleCandidate).build();

        entityManager.persist(sessionFound);
        entityManager.persist(sessionNotFound);
        entityManager.flush();

        // when
        List<Session> result = sessionRepository.findAll(
            SessionSpecifications.interviewerIdEquals(sessionFound.getInterviewerId()));

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(sessionFound));
    }

    @Test
    void CandidateIdEqualsShouldFilterByGivenCandidateId() {
        // given
        Candidate candidate1 = CandidateTestDataBuilder.aCandidate().build();
        Candidate candidate2 = CandidateTestDataBuilder.aCandidate().build();

        Session sessionFound = SessionTestDataBuilder.aSessionCompleted()
            .position(samplePosition).candidate(candidate1).build();
        Session sessionNotFound = SessionTestDataBuilder.aSessionCompleted()
            .position(samplePosition).candidate(candidate2).build();

        entityManager.persist(candidate1);
        entityManager.persist(candidate2);
        entityManager.persist(sessionFound);
        entityManager.persist(sessionNotFound);
        entityManager.flush();

        // when
        List<Session> result = sessionRepository.findAll(
            SessionSpecifications.CandidateIdEquals(
                sessionFound.getCandidate().getCandidateId()));

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(sessionFound));
    }

    @Test
    void allSpecShouldReturnAllWhenNull() {
        // given
        Session sessionFound = SessionTestDataBuilder.aSessionCompleted()
            .position(samplePosition).candidate(sampleCandidate).build();

        entityManager.persist(sessionFound);
        entityManager.flush();

        // when
        Specification<Session> spec = Specification
            .where(SessionSpecifications.scheduledAfter(null))
            .and(SessionSpecifications.scheduledBefore(null))
            .and(SessionSpecifications.hasPositionIn(null))
            .and(SessionSpecifications.statusEquals(null))
            .and(SessionSpecifications.interviewerIdEquals(null))
            .and(SessionSpecifications.CandidateIdEquals(null));

        List<Session> result = sessionRepository.findAll(spec);
            
        // then
        assertEquals(1, result.size());
    }
}
