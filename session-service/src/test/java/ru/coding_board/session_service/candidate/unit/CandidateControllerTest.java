package ru.coding_board.session_service.candidate.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import ru.coding_board.session_service.candidate.CandidateController;
import ru.coding_board.session_service.candidate.CandidateService;
import ru.coding_board.session_service.candidate.dto.CandidateCreateRequest;
import ru.coding_board.session_service.candidate.dto.CandidateFilterRequest;
import ru.coding_board.session_service.candidate.dto.CandidatePatchRequest;
import ru.coding_board.session_service.candidate.dto.CandidateResponse;


@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidateService candidateService;

    @Test
    void shouldReturnCandidateById() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        CandidateResponse response = CandidateResponse.builder()
            .candidateId(id)
            .build();
        when(candidateService.getCandidateById(id)).thenReturn(response);

        // when then
        mockMvc.perform(get("/candidates/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.candidateId").value(id.toString()));
    }

    @Test
    void shouldFilterCandidates() throws Exception {
        // given
        when(candidateService.filterCandidates(any(CandidateFilterRequest.class), any(Pageable.class)))
            .thenReturn(Page.empty());

        // when then
        mockMvc.perform(get("/candidates")
                .param("fullName", "Иван")
                .param("isActive", "true"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldPassPageableParamsToService() throws Exception {
        // given
        when(candidateService.filterCandidates(any(), any())).thenReturn(Page.empty());

        // when then
        mockMvc.perform(get("/candidates").param("page", "1").param("size", "5"))
            .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(candidateService).filterCandidates(any(), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void shouldCreateCandidate() throws Exception {
        // given
        CandidateCreateRequest request = new CandidateCreateRequest(
            "Иван", "Иванов", null, "ivan@test.com", null, null);
        when(candidateService.createCandidate(any(CandidateCreateRequest.class)))
            .thenReturn(CandidateResponse.builder().candidateId(UUID.randomUUID()).build());

        // when then
        mockMvc.perform(post("/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid() throws Exception {
        // given
        String invalidJson = "{}";

        // when then
        mockMvc.perform(post("/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(candidateService, never()).createCandidate(any());
    }

    @Test
    void shouldPatchCandidate() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        CandidatePatchRequest request = new CandidatePatchRequest(
            "Пётр", null, null, null, null, null, null);
        when(candidateService.patchCandidate(eq(id), any(CandidatePatchRequest.class)))
            .thenReturn(CandidateResponse.builder().candidateId(id).name("Пётр").build());

        // when then
        mockMvc.perform(patch("/candidates/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}