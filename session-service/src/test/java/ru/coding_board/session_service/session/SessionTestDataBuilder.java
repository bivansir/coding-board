package ru.coding_board.session_service.session;

import java.time.LocalDateTime;
import java.util.UUID;

import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.position.Position;

public final class SessionTestDataBuilder {
    private SessionTestDataBuilder() {};

    public static Session.SessionBuilder aSessionScheduledWithId() {
        return Session.builder()
            .sessionId(UUID.randomUUID())
            .sessionStatus(SessionStatus.SCHEDULED)
            .position(minimalPosition())
            .candidate(minimalCandidate())
            .interviewerId(UUID.randomUUID())
            .inviteToken("TestToken")
            .scheduledAt(LocalDateTime.now());
    }

    public static Session.SessionBuilder aSessionScheduled() {
        return Session.builder()
            .sessionStatus(SessionStatus.SCHEDULED)
            .position(minimalPosition())
            .candidate(minimalCandidate())
            .interviewerId(UUID.randomUUID())
            .inviteToken(UUID.randomUUID().toString())
            .scheduledAt(LocalDateTime.now());
    }

    public static Session.SessionBuilder aSessionCompletedWithId() {
        LocalDateTime now = LocalDateTime.now();

        return Session.builder()
            .sessionId(UUID.randomUUID())
            .sessionStatus(SessionStatus.COMPLETED)
            .position(minimalPosition())
            .candidate(minimalCandidate())
            .interviewerId(UUID.randomUUID())
            .inviteToken("TestToken")
            .scheduledAt(now.minusDays(1))
            .startedAt(now.minusDays(1))
            .completedAt(now.minusHours(23));
    }

    public static Session.SessionBuilder aSessionCompleted() {
        LocalDateTime now = LocalDateTime.now();

        return Session.builder()
            .sessionStatus(SessionStatus.COMPLETED)
            .position(minimalPosition())
            .candidate(minimalCandidate())
            .interviewerId(UUID.randomUUID())
            .inviteToken(UUID.randomUUID().toString())
            .scheduledAt(now.minusDays(1))
            .startedAt(now.minusDays(1))
            .completedAt(now.minusHours(23));
    }

    private static Position minimalPosition() {
        return Position.builder()
        .positionId(UUID.randomUUID())
        .build();
    }

    private static Candidate minimalCandidate() {
        return Candidate.builder()
        .candidateId(UUID.randomUUID())
        .build();
    }
}
