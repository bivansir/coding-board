package ru.coding_board.session_service.note.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import ru.coding_board.session_service.note.NoteController;
import ru.coding_board.session_service.note.NoteService;
import ru.coding_board.session_service.note.dto.NoteCreateRequest;
import ru.coding_board.session_service.note.dto.NoteResponse;
import ru.coding_board.session_service.note.dto.NotesResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private ObjectMapper objectMapper;

    @MockitoBean 
    private NoteService noteService;

    @Test
    void shouldReturnNotesBySessionId() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        NotesResponse response = new NotesResponse(sessionId, List.of());
        when(noteService.getNotes(sessionId)).thenReturn(response);

        // when then
        mockMvc.perform(get("/sessions/{id}/note", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(sessionId.toString()));
    }

    @Test
    void shouldCreateNote() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        NoteCreateRequest request = new NoteCreateRequest("Some note content", LocalDateTime.now());
        when(noteService.createNote(eq(sessionId), any(NoteCreateRequest.class)))
            .thenReturn(NoteResponse.builder().noteId(UUID.randomUUID()).build());

        // when then
        mockMvc.perform(post("/sessions/{id}/note", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenCreateNoteRequestIsInvalid() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        String invalidJson = "{}";

        // when then
        mockMvc.perform(post("/sessions/{id}/note", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(noteService, never()).createNote(any(), any());
    }

    @Test
    void shouldDeleteNote() throws Exception {
        // given
        UUID sessionId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        // when then
        mockMvc.perform(delete("/sessions/{sessionId}/note/{noteId}", sessionId, noteId))
            .andExpect(status().isNoContent());

        verify(noteService).deleteNote(sessionId, noteId);
    }
}
