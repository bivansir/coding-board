package ru.coding_board.session_service.common.exception;

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

import jakarta.websocket.Session;
import ru.coding_board.session_service.common.enums.SessionStatus;
import ru.coding_board.session_service.session.SessionController;
import ru.coding_board.session_service.session.SessionService;

@WebMvcTest(controllers = SessionController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private SessionService sessionService;

    void shouldMap404ForEntityNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(sessionService.getSessionById(id))
            .thenThrow(new ServiceEntityNotFoundException(Session.class, id));

        mockMvc.perform(get("/sessions/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldMap409ForInvalidTransition() throws Exception {
        UUID id = UUID.randomUUID();
        when(sessionService.startSession(id))
            .thenThrow(new InvalidSessionStatusException(id, SessionStatus.ONGOING));

        mockMvc.perform(patch("/sessions/{id}/start", id))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldMap400ForMissingRequestParameter() throws Exception {
        mockMvc.perform(post("/sessions"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldMap500ForUnexpectedException() throws Exception {
        UUID id = UUID.randomUUID();
        when(sessionService.getSessionById(id)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/sessions/{id}", id))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred")); // не раскрывает "boom"
    }

    @Test
    void shouldMap400ForValidationFailure() throws Exception {
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
