package ru.coding_board.session_service.session;

import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.position.Position;
import ru.coding_board.session_service.position.PositionMapper;
import ru.coding_board.session_service.session.dto.SessionCancellationCreateRequest;
import ru.coding_board.session_service.session.dto.SessionCancellationResponse;
import ru.coding_board.session_service.session.dto.SessionCreateRequest;
import ru.coding_board.session_service.session.dto.SessionResponse;

public class SessionBuilder {
    private SessionBuilder() {}

    // status и token в сервисе
    public static Session buildSession(
        SessionCreateRequest request, 
        Position position, 
        Candidate candidate) {
        return Session.builder()
            .position(position)
            .candidate(candidate)
            .interviewerId(request.interviewerId())
            .scheduledAt(request.scheduledAt())
            .build();
    }

    public static SessionCancellation buildSessionCancellation(
        Session session,
        SessionCancellationCreateRequest request) {
        return SessionCancellation.builder()
            .session(session)
            .cancellationReason(request.cancellationReason())
            .hrId(request.hrId())
            .cancellationTime(request.cancellationTime())
            .build();
    }

    public static SessionResponse buildResponse(
        Session session,
        Position position) {
        return(
            SessionResponse.builder()
            .sessionId(session.getSessionId())
            .status(session.getSessionStatus())
            .positionResponse(PositionMapper.buildResponse(position))
            .candidateId(session.getCandidate().getCandidateId())
            .interviewerId(session.getInterviewerId())
            .inviteToken(session.getInviteToken())
            .scheduledAt(session.getScheduledAt())
            .completedAt(session.getCompletedAt())
            .startedAt(session.getStartedAt())
            .build()
        );
    }

    public static SessionResponse buildResponse(
        Session session,
        SessionCancellation sessionCancellation,
        Position position) {
            return(
                buildResponse(session, position)
                .toBuilder()
                .sessionCancellation(
                    SessionCancellationResponse.builder()
                        .cancellationReason(sessionCancellation.getCancellationReason())
                        .hrId(sessionCancellation.getHrId())
                        .cancellationTime(sessionCancellation.getCancellationTime())
                        .build()
                )
                .build()
            );
    }
}
