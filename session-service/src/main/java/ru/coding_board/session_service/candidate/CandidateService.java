package ru.coding_board.session_service.candidate;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.coding_board.session_service.candidate.dto.CandidateCreateRequest;
import ru.coding_board.session_service.candidate.dto.CandidateFilterRequest;
import ru.coding_board.session_service.candidate.dto.CandidateListItemResponse;
import ru.coding_board.session_service.candidate.dto.CandidatePatchRequest;
import ru.coding_board.session_service.candidate.dto.CandidateResponse;
import ru.coding_board.session_service.common.util.RepositoryUtils;

@RequiredArgsConstructor
@Service
public class CandidateService {
    private static final int MIN_QUERY_LENGTH = 3;
    
    private final CandidateNormalizer candidateNormalizer;
    private final CandidateRepository candidateRepository;


    public CandidateResponse getCandidateById(UUID id) {
        return CandidateMapper.buildResponse(getById(id));
    }

    public Page<CandidateListItemResponse> filterCandidates(
        CandidateFilterRequest request,
        Pageable pageable) {

        String searchString = normalizeSearchQuery(request.fullName());

        if (searchString.length() <= MIN_QUERY_LENGTH) {
                return candidateRepository.searchByIsActive(request.isActive(), pageable);
            }

        return candidateRepository.searchByFullNameAndIsActive(
            searchString, request.isActive(), pageable);
    }

    @Transactional
    public CandidateResponse createCandidate(CandidateCreateRequest request) {
        CandidateCreateRequest normalized = new CandidateCreateRequest(
            candidateNormalizer.normalizeFioPart(request.name()),
            candidateNormalizer.normalizeFioPart(request.surname()),
            candidateNormalizer.normalizeFioPart(request.patronymic()),
            request.email(),
            request.phoneNumber(),
            request.resumeId()
        );

        Candidate candidate = CandidateMapper.buildCandidate(normalized);
        candidate.setIsActive(true);

        return CandidateMapper.buildResponse(candidateRepository.save(candidate));
    }

    @Transactional
    public CandidateResponse patchCandidate(UUID candidateId, CandidatePatchRequest request) {
        Candidate candidate = getById(candidateId);

        if (request.name() != null) candidate.setName(
            candidateNormalizer.normalizeFioPart(request.name()));
        if (request.surname() != null) candidate.setSurname(
            candidateNormalizer.normalizeFioPart(request.surname()));
        if (request.patronymic() != null) candidate.setPatronymic(
            candidateNormalizer.normalizeFioPart(request.patronymic()));
        if (request.email() != null) candidate.setEmail(
            request.email());
        if (request.resumeId() != null) candidate.setResumeId(
            request.resumeId());
        if (request.phoneNumber() != null) candidate.setPhoneNumber(
            request.phoneNumber());
        if (request.isActive() != null) candidate.setIsActive(
            request.isActive());

        Candidate saved = candidateRepository.save(candidate);

        return CandidateMapper.buildResponse(saved);
    }

     private Candidate getById(UUID id) { // Использовать только в транзакции
        Candidate candidate = RepositoryUtils.findOrThrow(
            candidateRepository, id, Candidate.class);

        return candidate;
    }

    private String normalizeSearchQuery(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }
        return query.trim().toLowerCase().replaceAll("\\s{2,}", " ");
    }
}
