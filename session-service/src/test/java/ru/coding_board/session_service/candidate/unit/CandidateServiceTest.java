package ru.coding_board.session_service.candidate.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ru.coding_board.session_service.candidate.Candidate;
import ru.coding_board.session_service.candidate.CandidateMapper;
import ru.coding_board.session_service.candidate.CandidateNormalizer;
import ru.coding_board.session_service.candidate.CandidateRepository;
import ru.coding_board.session_service.candidate.CandidateService;
import ru.coding_board.session_service.candidate.CandidateTestDataBuilder;
import ru.coding_board.session_service.candidate.dto.CandidateCreateRequest;
import ru.coding_board.session_service.candidate.dto.CandidateFilterRequest;
import ru.coding_board.session_service.candidate.dto.CandidateListItemResponse;
import ru.coding_board.session_service.candidate.dto.CandidatePatchRequest;
import ru.coding_board.session_service.candidate.dto.CandidateResponse;
import ru.coding_board.session_service.common.exception.ServiceEntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {
    @InjectMocks 
    private CandidateService candidateService;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CandidateNormalizer candidateNormalizer;
    
    @Test 
    void shouldReturnCandidateById() {
        // given
        Candidate candidate = CandidateTestDataBuilder.aCandidate().build();
        UUID candidateId = candidate.getCandidateId();
        when(candidateRepository.findById(candidateId))
            .thenReturn(Optional.of(candidate));

        // when
        CandidateResponse result = candidateService.getCandidateById(candidateId);

        // then
        assertEquals(CandidateMapper.buildResponse(candidate), result);
    }

    @Test
    void shouldThrowWhenCandidateNotFound() {
        // given
        UUID candidateId = UUID.randomUUID();
        when(candidateRepository.findById(candidateId))
            .thenReturn(Optional.empty());

        // when then
        assertThrows(ServiceEntityNotFoundException.class,
            () -> candidateService.getCandidateById(candidateId));
    }

    @Test 
    void shouldUseFuzzySearchWithNormalizedQueryWhenLongEnough() {
        // given
        CandidateFilterRequest request = 
            new CandidateFilterRequest(" ИЛЬЯ   СЕМЁноВ", true);
        PageRequest pageable = PageRequest.of(1, 5);

        when(candidateRepository.searchByFullNameAndIsActive(
            "илья семёнов", request.isActive(), pageable)).thenReturn(Page.empty());

        // when
        candidateService.filterCandidates(request, pageable);

        // then
        verify(candidateRepository).searchByFullNameAndIsActive(
            "илья семёнов", request.isActive(), pageable);
        verify(candidateRepository, never()).searchByIsActive(anyBoolean(), any());
    }

    @Test 
    void shouldUseCatalogSearchWhenNormalizedQueryIsTooShort() {
        // given
        CandidateFilterRequest request = 
            new CandidateFilterRequest("  пып ", true);
        PageRequest pageable = PageRequest.of(1, 5);

        when(candidateRepository.searchByIsActive(
            request.isActive(), pageable)).thenReturn(Page.empty());

        // when
        candidateService.filterCandidates(request, pageable);

        // then
        verify(candidateRepository).searchByIsActive(true, pageable);
        verify(candidateRepository, never()).searchByFullNameAndIsActive(
            any(), anyBoolean(), any());
    }

    @Test
    void shouldFilterCandidatesAndReturnItsResult() {
        // given
        CandidateFilterRequest request = 
            new CandidateFilterRequest(" ИЛЬЯ   СЕМЁноВ", true);
        PageRequest pageable = PageRequest.of(1, 5);
        Page<CandidateListItemResponse> expectedPage = new PageImpl<>(List.of(
            new CandidateListItemResponse(UUID.randomUUID(), "Илья", "Семёнов",
            null, "test@gmail.com")));

        when(candidateRepository.searchByFullNameAndIsActive("илья семёнов", true, pageable))
            .thenReturn(expectedPage);

        // when
        Page<CandidateListItemResponse> result = candidateService.filterCandidates(request, pageable);

        // then
        assertEquals(expectedPage, result);
}

    @Test
    void shouldSaveCandidateWithNormalizedFieldsFromRequest() {
        // given
        CandidateCreateRequest request = new CandidateCreateRequest(
            "МИХАил", "  салтыков-щедрин ", null,
            "test@gmail.com", null, null);

        when(candidateNormalizer.normalizeFioPart("МИХАил"))
            .thenReturn("Михаил");
        when(candidateNormalizer.normalizeFioPart("  салтыков-щедрин "))
            .thenReturn("Салтыков-Щедрин");
        when(candidateNormalizer.normalizeFioPart(null))
            .thenReturn(null);
        when(candidateRepository.save(any(Candidate.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        candidateService.createCandidate(request);

        // then
        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);
        verify(candidateRepository).save(captor.capture());
        Candidate saved = captor.getValue();

        assertEquals("Михаил", saved.getName());
        assertEquals("Салтыков-Щедрин", saved.getSurname());
        assertNull(saved.getPatronymic());
        assertEquals(request.email(), saved.getEmail());
        assertEquals(request.phoneNumber(), saved.getPhoneNumber());
        assertEquals(request.resumeId(), saved.getResumeId());
    }

    @Test
    void shouldAlwaysSetNewCandidateAsActive() {
        // given
        CandidateCreateRequest request = new CandidateCreateRequest(
            "Иван", "Иванов", null,
            "ivan@test.com", null, null);

        when(candidateNormalizer.normalizeFioPart(anyString()))
            .thenAnswer(inv -> inv.getArgument(0));
        when(candidateNormalizer.normalizeFioPart(null))
            .thenReturn(null);
        when(candidateRepository.save(any(Candidate.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        candidateService.createCandidate(request);

        // then
        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);
        verify(candidateRepository).save(captor.capture());

        assertTrue(captor.getValue().getIsActive());
    }

    @Test
    void shouldReturnResponseBuiltFromSavedCandidate() {
        // given
        CandidateCreateRequest request = new CandidateCreateRequest(
            "Set in Request", null, null,
            null, null, null);
        Candidate saved = CandidateTestDataBuilder.aCandidate()
        .name("Set in DB Layer").build();

        when(candidateNormalizer.normalizeFioPart(
            anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(candidateNormalizer.normalizeFioPart(
            null)).thenReturn(null);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(saved);

        // when
        CandidateResponse response = candidateService.createCandidate(request);

        // then
        assertEquals(saved.getName(), response.name());
        assertNotEquals(request.name(), response.name());
        assertEquals(saved.getCandidateId(), response.candidateId());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        // given
        Candidate existing = CandidateTestDataBuilder.aCandidateWithId().build();
        String originalResumeId = existing.getResumeId();
        String originalEmail = existing.getEmail(); 

        CandidatePatchRequest request = new CandidatePatchRequest(
            "МИХАил", null, null,
            null, null, null, null);

        when(candidateNormalizer.normalizeFioPart("МИХАил")).thenReturn("Михаил");
        when(candidateRepository.findById(existing.getCandidateId()))
            .thenReturn(Optional.of(existing));
        when(candidateRepository.save(any(Candidate.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        candidateService.patchCandidate(existing.getCandidateId(), request);

        // then
        assertEquals("Михаил", existing.getName());
        assertEquals("Иванов", existing.getSurname());
        assertEquals("Иванович", existing.getPatronymic());
        assertEquals(originalEmail, existing.getEmail());
        assertEquals("+79991234567", existing.getPhoneNumber());
        assertEquals(originalResumeId, existing.getResumeId());
    }

    @Test
    void shouldOnlyNormalizeFioFieldsNotOtherStringFields() {
        // given
        Candidate existing = CandidateTestDataBuilder.aCandidateWithId().build();
        CandidatePatchRequest request = new CandidatePatchRequest(
            "МИХАил", " салтыков-щедрин", "   Ильич",
            "new@gmail.com", "+70001112233", "resume-2",
            null);

        when(candidateNormalizer.normalizeFioPart("МИХАил")).thenReturn("Михаил");
        when(candidateNormalizer.normalizeFioPart(" салтыков-щедрин"))
            .thenReturn("Салтыков-Щедрин");
        when(candidateNormalizer.normalizeFioPart("   Ильич"))
            .thenReturn("Ильич");
        when(candidateRepository.findById(existing.getCandidateId()))
            .thenReturn(Optional.of(existing));
        when(candidateRepository.save(any(Candidate.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        candidateService.patchCandidate(existing.getCandidateId(), request);

        // then
        assertEquals("Михаил", existing.getName());
        assertEquals("Салтыков-Щедрин", existing.getSurname());
        assertEquals("Ильич", existing.getPatronymic());

        assertEquals("new@gmail.com", existing.getEmail());
        assertEquals("+70001112233", existing.getPhoneNumber());
        assertEquals("resume-2", existing.getResumeId());
        verify(candidateNormalizer, never()).normalizeFioPart("new@gmail.com");
        verify(candidateNormalizer, never()).normalizeFioPart("+70001112233");
        verify(candidateNormalizer, never()).normalizeFioPart("resume-2");
    }
}
