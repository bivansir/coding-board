package ru.coding_board.session_service.feedback.unit;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import ru.coding_board.session_service.common.enums.Decision;
import ru.coding_board.session_service.feedback.FeedbackController;
import ru.coding_board.session_service.feedback.FeedbackService;
import ru.coding_board.session_service.feedback.dto.FeedbackCreateRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackPatchRequest;
import ru.coding_board.session_service.feedback.dto.FeedbackResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private ObjectMapper objectMapper;

    @MockitoBean 
    private FeedbackService feedbackService;

    @Test
    void shouldReturnFeedbackBySessionId() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        FeedbackResponse response = FeedbackResponse.builder()
            .feedbackId(UUID.randomUUID())
            .sessionId(sessionId)
            .build();
        when(feedbackService.getFeedbackBySessionId(sessionId)).thenReturn(response);

        // when then
        mockMvc.perform(get("/sessions/{id}/feedback", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(sessionId.toString()));
    }

    @Test
    void shouldCreateFeedback() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        FeedbackCreateRequest request = new FeedbackCreateRequest(null, Decision.QUESTIONABLE, "comment");
        when(feedbackService.createFeedback(eq(sessionId), any(FeedbackCreateRequest.class)))
            .thenReturn(FeedbackResponse.builder().feedbackId(UUID.randomUUID()).build());

        // when then
        mockMvc.perform(post("/sessions/{id}/feedback", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreateFeedbackRequestIsInvalid() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        String invalidJson = "{}";

        // when then
        mockMvc.perform(post("/sessions/{id}/feedback", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(feedbackService, never()).createFeedback(any(), any());
    }

    @Test
    void shouldPatchFeedback() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        FeedbackPatchRequest request = new FeedbackPatchRequest(null, Decision.FAILED, null);
        when(feedbackService.patchFeedback(eq(sessionId), any(FeedbackPatchRequest.class)))
            .thenReturn(FeedbackResponse.builder().decision(Decision.FAILED).build());

        // when then
        mockMvc.perform(patch("/sessions/{id}/feedback", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}
