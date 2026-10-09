package ru.coding_board.session_service.session;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateRepository;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.common.exception.InvalidSessionStatusException;
import ru.coding_board.session_service.common.util.RepositoryUtils;
import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionRepository;
import ru.coding_board.session_service.session.dto.SessionCancellationCreateRequest;
import ru.coding_board.session_service.session.dto.SessionCreateRequest;
import ru.coding_board.session_service.session.dto.SessionFilterRequest;
import ru.coding_board.session_service.session.dto.SessionResponse;
import ru.coding_board.session_service.session.dto.SessionScheduledPatchRequest;

@RequiredArgsConstructor
@Service
@Slf4j 
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionCancellationRepository sessionCancellationRepository;
    private final PositionRepository positionRepository;
    private final CandidateRepository candidateRepository;

    private final SessionInviteTokenGenerator tokenGenerator;

    public SessionResponse getSessionById(UUID id) {
        Session session = getById(id);

        return SessionBuilder.buildResponse(session,  session.getPosition());
    }

    public Page<SessionResponse> filterSessions(SessionFilterRequest request, Pageable pageable) {
        Specification<Session> spec = Specification
            .where(SessionSpecifications.scheduledAfter(request.startDate()))
            .and(SessionSpecifications.scheduledBefore(request.endDate()))
            .and(SessionSpecifications.hasPositionIn(request.positions()))
            .and(SessionSpecifications.statusEquals(request.status()))
            .and(SessionSpecifications.interviewerIdEquals(request.interviewerId()))
            .and(SessionSpecifications.CandidateIdEquals(request.candidateId()));

        return sessionRepository.findAll(spec, pageable).map(
            s -> SessionBuilder.buildResponse(s, s.getPosition()));
    }

    @Transactional
    public SessionResponse createSession(SessionCreateRequest request) {
        Position position = getPositionById(request.positionId());
        Candidate candidate = getCandidateById(request.candidateId());

        Session session = SessionBuilder.buildSession(request, position, candidate);
        session.setInviteToken(tokenGenerator.generate());
        session.setSessionStatus(SessionStatus.SCHEDULED);

        Session saved = sessionRepository.save(session);

        return SessionBuilder.buildResponse(
            saved, saved.getPosition());
    }

    @Transactional
    public SessionResponse patchScheduledSession(UUID sessionId, SessionScheduledPatchRequest request) {
        Session session = getById(sessionId);

        if (session.getSessionStatus() != SessionStatus.SCHEDULED) {
            throw new InvalidSessionStatusException(sessionId, session.getSessionStatus());
        }

        if (request.positionID() != null) session.setPosition(
            getPositionById(request.positionID()));
        if (request.interviewerID() != null) session.setInterviewerId(
            request.interviewerID());
        if (request.candidateID() != null) session.setCandidate(
            getCandidateById(request.candidateID()));
        if (request.scheduledAt() != null) session.setScheduledAt(
            request.scheduledAt());

        Session saved = sessionRepository.save(session);

        return SessionBuilder.buildResponse(
            saved, saved.getPosition());
    }

    @Transactional
    public String genInviteToken(UUID sessionId) {
        Session session = getById(sessionId);

        String newInviteToken = tokenGenerator.generate();
        session.setInviteToken(newInviteToken);
        
        sessionRepository.save(session);
        log.info("Invite token regenerated for session {}", sessionId);
        return newInviteToken;
    }

    @Transactional
    public SessionResponse cancelSession(UUID sessionId, SessionCancellationCreateRequest request) {
        Session session = getById(sessionId);
        SessionCancellation sessionCancellation = SessionBuilder.buildSessionCancellation(
            session, request);

        sessionCancellationRepository.save(sessionCancellation);

        return SessionBuilder.buildResponse(
            transitSession(session, SessionStatus.CANCELLED),
            sessionCancellation, session.getPosition());
    }

    @Transactional 
    public SessionResponse startSession(UUID sessionId) {
        Session session = getById(sessionId);

        return SessionBuilder.buildResponse(
            transitSession(session, SessionStatus.ONGOING), session.getPosition());
    }

    @Transactional 
    public SessionResponse completeSession(UUID sessionId, LocalDateTime time) {
        Session session = getById(sessionId);

        return SessionBuilder.buildResponse(
            completeSessionTransition(session, time), session.getPosition());
    }

    private Session getById(UUID id) {
        Session session = RepositoryUtils.findOrThrow(
            sessionRepository, id, Session.class);
        
        return session;
    }

    private Position getPositionById(UUID id) {
        Position position = RepositoryUtils.findOrThrow(
            positionRepository, id, Position.class);
        
        return position;
    }

    private Candidate getCandidateById(UUID id) {
        Candidate candidate = RepositoryUtils.findOrThrow(
            candidateRepository, id, Candidate.class);
        
        return candidate;
    }

    private Session transitSession(Session session, SessionStatus targetStatus) {
        validateSessionTransition(session, targetStatus);
        session.setSessionStatus(targetStatus);
        return sessionRepository.save(session);
    }

    private Session completeSessionTransition(Session session, LocalDateTime time) {
        validateSessionTransition(session, SessionStatus.COMPLETED);
        session.setCompletedAt(time != null ? time : LocalDateTime.now());
        session.setSessionStatus(SessionStatus.COMPLETED);
        return sessionRepository.save(session);
    }

    private void validateSessionTransition(Session session, SessionStatus targetStatus) {
        SessionStatus status = session.getSessionStatus();

        if ((targetStatus == SessionStatus.SCHEDULED) ||
            (status == SessionStatus.SCHEDULED && targetStatus == SessionStatus.COMPLETED) ||
            (status == SessionStatus.COMPLETED) ||
            (status == SessionStatus.CANCELLED)) {
                throw new InvalidSessionStatusException(session.getSessionId(), status); 
            }
    } 
}
