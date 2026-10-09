package ru.coding_board.session_service.session.session_task.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.coding_board.session_service.common.enums.ProgrammingLanguage;
import ru.coding_board.session_service.session.session_task.SessionTaskController;
import ru.coding_board.session_service.session.session_task.SessionTaskService;
import ru.coding_board.session_service.session.session_task.dto.SessionTaskResponse;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksCreateRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksPatchRequest;
import ru.coding_board.session_service.session.session_task.dto.SessionTasksResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(SessionTaskController.class)
class SessionTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean 
    private SessionTaskService sessionTaskService;

    @Test
    void shouldReturnSessionTasksBySessionId() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        SessionTasksResponse response = new SessionTasksResponse(sessionId, List.of());
        when(sessionTaskService.getSessionTasksBySessionId(sessionId)).thenReturn(response);

        // when then
        mockMvc.perform(get("/sessions/{id}/task", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(sessionId.toString()));
    }

    @Test
    void shouldCreateSessionTasks() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        SessionTasksCreateRequest request = new SessionTasksCreateRequest(List.of(
            new SessionTaskResponse(UUID.randomUUID(), null, null)
        ));
        when(sessionTaskService.createSessionTasks(eq(sessionId), any(SessionTasksCreateRequest.class)))
            .thenReturn(new SessionTasksResponse(sessionId, List.of()));

        // when then
        mockMvc.perform(post("/sessions/{id}/task", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreateSessionTasksRequestIsInvalid() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();

        // when then
        mockMvc.perform(post("/sessions/{id}/task", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        verify(sessionTaskService, never()).createSessionTasks(any(), any());
    }

    @Test
    void shouldDeleteSessionTasks() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        List<UUID> taskIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // when then
        mockMvc.perform(delete("/sessions/{sessionId}/task", sessionId)
                .param("sessionTaskIds", taskIds.get(0).toString(), taskIds.get(1).toString()))
            .andExpect(status().isNoContent());

        verify(sessionTaskService).deleteSessionTasks(sessionId, taskIds);
    }

    @Test
    void shouldPatchSessionTasks() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        List<SessionTasksPatchRequest> request = List.of(
            new SessionTasksPatchRequest(UUID.randomUUID(), ProgrammingLanguage.PYTHON)
        );
        when(sessionTaskService.patchSessionTasks(eq(sessionId), anyList()))
            .thenReturn(new SessionTasksResponse(sessionId, List.of()));

        // when then
        mockMvc.perform(patch("/sessions/{id}/task", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenPatchSessionTasksRequestIsInvalid() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();

        // when then
        mockMvc.perform(patch("/sessions/{id}/task", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{}]"))
            .andExpect(status().isBadRequest());

        verify(sessionTaskService, never()).patchSessionTasks(any(), any());
    }
}
