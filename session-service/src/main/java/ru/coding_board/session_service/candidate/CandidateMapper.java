package ru.coding_board.session_service.candidate;

import ru.coding_board.session_service.candidate.dto.CandidateResponse;
import ru.coding_board.session_service.candidate.dto.CandidateCreateRequest;

public final class CandidateMapper {
    private CandidateMapper() {}

    // isActive ставит сервис
    public static Candidate buildCandidate(CandidateCreateRequest request) {
        return Candidate.builder()
            .name(request.name())
            .surname(request.surname())
            .patronymic(request.patronymic())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .resumeId(request.resumeId())
            .build();
    }

    public static CandidateResponse buildResponse(Candidate candidate) {
        return CandidateResponse.builder()
            .candidateId(candidate.getCandidateId())
            .name(candidate.getName())
            .surname(candidate.getSurname())
            .patronymic(candidate.getPatronymic())
            .email(candidate.getEmail())
            .phoneNumber(candidate.getPhoneNumber())
            .resumeId(candidate.getResumeId())
            .isActive(candidate.getIsActive())
            .build();
    }
}
