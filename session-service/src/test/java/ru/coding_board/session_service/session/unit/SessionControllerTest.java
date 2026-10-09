package ru.coding_board.session_service.session.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.coding_board.session_service.session.SessionController;
import ru.coding_board.session_service.session.SessionService;
import ru.coding_board.session_service.session.dto.SessionCancellationCreateRequest;
import ru.coding_board.session_service.session.dto.SessionCreateRequest;
import ru.coding_board.session_service.session.dto.SessionFilterRequest;
import ru.coding_board.session_service.session.dto.SessionResponse;
import ru.coding_board.session_service.session.dto.SessionScheduledPatchRequest;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean 
    private SessionService sessionService;

    @Test
    void shouldReturnSessionById() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        when(sessionService.getSessionById(id)).thenReturn(SessionResponse.builder().sessionId(id).build());

        // when then
        mockMvc.perform(get("/sessions/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(id.toString()));
    }

    @Test
    void shouldFilterSessions() throws Exception {
        // given
        when(sessionService.filterSessions(any(SessionFilterRequest.class), any(Pageable.class)))
            .thenReturn(Page.empty());

        // when then
        mockMvc.perform(get("/sessions").param("status", "SCHEDULED"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCreateSession() throws Exception {
        // given
        SessionCreateRequest request = new SessionCreateRequest(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        when(sessionService.createSession(any(SessionCreateRequest.class)))
            .thenReturn(SessionResponse.builder().sessionId(UUID.randomUUID()).build());

        // when then
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreateSessionRequestIsInvalid() throws Exception {
        // when then
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        verify(sessionService, never()).createSession(any());
    }

    @Test
    void shouldPatchScheduledSession() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        SessionScheduledPatchRequest request = new
            SessionScheduledPatchRequest(UUID.randomUUID(), null, null, null);
        when(sessionService.patchScheduledSession(eq(id), any(SessionScheduledPatchRequest.class)))
            .thenReturn(SessionResponse.builder().sessionId(id).build());

        // when then
        mockMvc.perform(patch("/sessions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGenerateInviteToken() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        when(sessionService.genInviteToken(id)).thenReturn("secure-token-value");

        // when then
        mockMvc.perform(patch("/sessions/{id}/invite-token", id))
            .andExpect(status().isOk())
            .andExpect(content().string("secure-token-value"));
    }

    @Test
    void shouldCancelSession() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        SessionCancellationCreateRequest request = new
            SessionCancellationCreateRequest("Test cancellation", UUID.randomUUID(), LocalDateTime.now());
        when(sessionService.cancelSession(eq(id), any(SessionCancellationCreateRequest.class)))
            .thenReturn(SessionResponse.builder().sessionId(id).build());

        // when then
        mockMvc.perform(patch("/sessions/{id}/cancel", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenCancelRequestIsInvalid() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        // when then
        mockMvc.perform(patch("/sessions/{id}/cancel", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        verify(sessionService, never()).cancelSession(any(), any());
    }

    @Test
    void shouldStartSession() throws Exception {
        // givne
        UUID id = UUID.randomUUID();
        when(sessionService.startSession(id)).thenReturn(SessionResponse.builder().sessionId(id).build());

        // when then
        mockMvc.perform(patch("/sessions/{id}/start", id))
            .andExpect(status().isOk());
    }

    @Test
    void shouldEndSessionWithProvidedTime() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.of(2026, 1, 15, 10, 0);
        when(sessionService.completeSession(eq(id), eq(time)))
            .thenReturn(SessionResponse.builder().sessionId(id).build());

        // when then
        mockMvc.perform(patch("/sessions/{id}/end", id)
                .param("time", time.toString()))
            .andExpect(status().isOk());
    }

    @Test
    void shouldEndSessionWithoutTime() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        when(sessionService.completeSession(eq(id), isNull()))
            .thenReturn(SessionResponse.builder().sessionId(id).build());

        // when then
        mockMvc.perform(patch("/sessions/{id}/end", id))
            .andExpect(status().isOk());
    }
}
