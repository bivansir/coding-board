package ru.coding_board.session_service.candidate;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class CandidateTestDataBuilder {
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private CandidateTestDataBuilder() {}

    public static Candidate.CandidateBuilder aCandidate() {
        int id = COUNTER.incrementAndGet();
        return Candidate.builder()
            .name("Иван")
            .surname("Иванов")
            .patronymic("Иванович")
            .email("candidate" + id + "@test.com")
            .phoneNumber("+79991234567")
            .resumeId("resume-" + id)
            .isActive(true);
    }

    public static Candidate.CandidateBuilder aCandidateWithId() {
        int id = COUNTER.incrementAndGet();
        return Candidate.builder()
            .candidateId(UUID.randomUUID())
            .name("Иван")
            .surname("Иванов")
            .patronymic("Иванович")
            .email("candidate" + id + "@test.com")
            .phoneNumber("+79991234567")
            .resumeId("resume-" + id)
            .isActive(true);
    }
}