package ru.coding_board.session_service.candidate.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityManager;
import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateRepository;
import ru.coding_board.session_service.candidate.CandidateTestDataBuilder;
import ru.coding_board.session_service.candidate.dto.CandidateListItemResponse;
import ru.coding_board.session_service.util.AbstractIntegrationTest;


public class CandidateRepositoryTest extends AbstractIntegrationTest {
    @Autowired 
    private CandidateRepository candidateRepository;

    @Autowired
    private EntityManager entityManager;
        
    @Test
    void shouldProceedFuzzySearch() {
        // given
        entityManager.persist(CandidateTestDataBuilder.aCandidate()
            .name("Илья").surname("Семёнов").patronymic("Игоревич").build());
        entityManager.flush();

        // when
        Page<CandidateListItemResponse> found = candidateRepository.searchByFullNameAndIsActive(
            "Семенов ильч", true, PageRequest.of(0, 10)
        );

        // then
        assertEquals(1, found.getContent().size());
    }

    @Test
    void shouldProceedPrefixSearch() {
        // given
        entityManager.persist(CandidateTestDataBuilder.aCandidate()
                .name("Илья").surname("Семёнов").build());
        entityManager.persist(CandidateTestDataBuilder.aCandidate()
                .name("Василий").surname("Семченко").build());
        entityManager.persist(CandidateTestDataBuilder.aCandidate()
                .name("Андрей").surname("Сергеев").build());
        entityManager.flush();

        // when
        Page<CandidateListItemResponse> found = candidateRepository.searchByFullNameAndIsActive(
            "Сем", true, PageRequest.of(0, 10)
        );

        // then
        assertEquals(2, found.getContent().size());
    }

    @Test
    void shouldSearchByIsActive() {
        // given
        Candidate activeCandidate = CandidateTestDataBuilder.aCandidate().build();
        Candidate inActiveCandidate = CandidateTestDataBuilder.aCandidate().isActive(false).build();

        entityManager.persist(activeCandidate);
        entityManager.persist(inActiveCandidate);
        entityManager.flush();

        // when
        Page<CandidateListItemResponse> foundActive = candidateRepository.searchByIsActive(
            true, PageRequest.of(0, 10)
        );
        Page<CandidateListItemResponse> foundInActive = candidateRepository.searchByIsActive(
            false, PageRequest.of(0, 10)
        );

        // then
        assertEquals(activeCandidate.getCandidateId(), 
            foundActive.getContent().get(0).candidateId());
        assertEquals(inActiveCandidate.getCandidateId(), 
            foundInActive.getContent().get(0).candidateId());
    }

    @Test
    void shouldRespectPageable() {
        // given
        for (int i = 0; i < 5; i++) {
            entityManager.persist(CandidateTestDataBuilder.aCandidate().build());
        }
        entityManager.flush();

        // when
        Page<CandidateListItemResponse> firstPage = candidateRepository
            .searchByIsActive(true, PageRequest.of(0, 2));
        Page<CandidateListItemResponse> secondPage = candidateRepository
            .searchByIsActive(true, PageRequest.of(1, 2));

        // then
        assertEquals(2, firstPage.getContent().size());
        assertEquals(2, secondPage.getContent().size());
        assertEquals(5, firstPage.getTotalElements());
        assertNotEquals(
            firstPage.getContent().get(0).candidateId(),
            secondPage.getContent().get(0).candidateId()
        );
    }
}
