package ru.coding_board.session_service.candidate.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateMapper;
import ru.coding_board.session_service.candidate.CandidateTestDataBuilder;
import ru.coding_board.session_service.candidate.dto.CandidateResponse;
import ru.coding_board.session_service.candidate.dto.CandidateCreateRequest;

class CandidateMapperTest {
    @Test
    void shouldMapCreateRequestToCandidate() {
        // given
        CandidateCreateRequest request = new CandidateCreateRequest(
            "Иван", "Иванов",
            "Иванович", "ivan@test.com",
            "+79991234567", "resume-1");

        // when
        Candidate candidate = CandidateMapper.buildCandidate(request);

        // then
        assertEquals(request.name(), candidate.getName());
        assertEquals(request.surname(), candidate.getSurname());
        assertEquals(request.patronymic(), candidate.getPatronymic());
        assertEquals(request.email(), candidate.getEmail());
        assertEquals(request.phoneNumber(), candidate.getPhoneNumber());
        assertEquals(request.resumeId(), candidate.getResumeId());
    }

    @Test
    void shouldMapCandidateToResponse() {
        // given
        Candidate candidate = CandidateTestDataBuilder.aCandidate().build();

        // when
        CandidateResponse response = CandidateMapper.buildResponse(candidate);

        // then
        assertEquals(candidate.getCandidateId(), response.candidateId());
        assertEquals(candidate.getName(), response.name());
        assertEquals(candidate.getSurname(), response.surname());
        assertEquals(candidate.getPatronymic(), response.patronymic());
        assertEquals(candidate.getEmail(), response.email());
        assertEquals(candidate.getPhoneNumber(), response.phoneNumber());
        assertEquals(candidate.getResumeId(), response.resumeId());
        assertTrue(response.isActive());
    }
}
